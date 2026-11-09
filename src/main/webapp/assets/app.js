/* Placement Cell: small progressive enhancements. Every page still works without JavaScript. */
(function () {
  "use strict";
  const $ = (s, r = document) => r.querySelector(s);
  const $$ = (s, r = document) => Array.from(r.querySelectorAll(s));
  const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  /* ---------- toast ---------- */
  let toastEl, toastTimer;
  function toast(msg, isErr) {
    if (!toastEl) {
      toastEl = document.createElement("div");
      toastEl.className = "toast";
      toastEl.setAttribute("role", "status");
      document.body.appendChild(toastEl);
    }
    toastEl.textContent = msg;
    toastEl.classList.toggle("err", !!isErr);
    toastEl.classList.add("show");
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toastEl.classList.remove("show"), 2600);
  }

  function confetti(x, y) {
    if (reduceMotion) return;
    const colors = ["#c2410c", "#1f6b47", "#1d1c1a", "#e9b949", "#1d4e89"];
    for (let i = 0; i < 26; i++) {
      const p = document.createElement("i");
      p.className = "confetti";
      p.style.left = x + "px";
      p.style.top = y + "px";
      p.style.background = colors[i % colors.length];
      p.style.setProperty("--x", (Math.random() * 260 - 130) + "px");
      p.style.setProperty("--y", (Math.random() * 180 + 60) + "px");
      p.style.setProperty("--r", (Math.random() * 720 - 360) + "deg");
      document.body.appendChild(p);
      setTimeout(() => p.parentNode && p.parentNode.removeChild(p), 1200);
    }
  }

  /* ---------- staggered reveal ---------- */
  $$(".ticket, .card, .kcard, .ledger .cell, .pin").forEach((el, i) => {
    el.classList.add("reveal");
    el.style.setProperty("--i", Math.min(i, 12));
  });

  /* ---------- drag-and-drop hiring board ---------- */
  const board = $(".board");
  if (board) {
    const endpoint = board.dataset.endpoint, csrf = board.dataset.csrf;
    let dragged = null;

    const refreshCounts = () => $$(".col", board).forEach(col => {
      const n = $$(".kcard", col).length;
      $(".n", col).textContent = n;
      $(".col-empty", col).hidden = n > 0;
    });

    async function move(card, status) {
      const from = card.closest(".col"), to = $(`.col[data-status="${status}"]`, board);
      if (!to || from === to) return;
      const sel = $("select", card);
      $(".col-body", to).prepend(card);
      if (sel) sel.value = status;
      refreshCounts();
      card.classList.remove("just-moved"); void card.offsetWidth; card.classList.add("just-moved");
      try {
        const body = new URLSearchParams({ _csrf: csrf, id: card.dataset.id, status });
        const r = await fetch(endpoint, { method: "POST", body, headers: { "X-Requested-With": "fetch" }, credentials: "same-origin" });
        if (!r.ok) throw new Error(String(r.status));
        toast(card.dataset.name + " moved to " + status.toLowerCase());
        if (status === "SELECTED") {
          const b = card.getBoundingClientRect();
          confetti(b.left + b.width / 2, b.top + 10);
        }
      } catch (e) {
        $(".col-body", from).prepend(card);
        if (sel) sel.value = from.dataset.status;
        refreshCounts();
        toast("Could not save that move. Refresh and try again.", true);
      }
    }

    $$(".kcard", board).forEach(card => {
      card.addEventListener("dragstart", e => {
        dragged = card;
        card.classList.add("dragging");
        e.dataTransfer.effectAllowed = "move";
        e.dataTransfer.setData("text/plain", card.dataset.id);
      });
      card.addEventListener("dragend", () => {
        card.classList.remove("dragging");
        dragged = null;
        $$(".col.over", board).forEach(c => c.classList.remove("over"));
      });
      const sel = $("select", card);
      if (sel) sel.addEventListener("change", () => move(card, sel.value));
    });
    $$(".col", board).forEach(col => {
      col.addEventListener("dragover", e => { if (dragged) { e.preventDefault(); col.classList.add("over"); } });
      col.addEventListener("dragleave", e => { if (!col.contains(e.relatedTarget)) col.classList.remove("over"); });
      col.addEventListener("drop", e => { e.preventDefault(); col.classList.remove("over"); if (dragged) move(dragged, col.dataset.status); });
    });
    refreshCounts();
  }

  /* ---------- view tabs (board / details) ---------- */
  $$("[data-view-tabs]").forEach(tabs => {
    const panes = $$("section[data-view]");
    const show = name => {
      $$("a", tabs).forEach(a => a.classList.toggle("on", a.dataset.view === name));
      panes.forEach(p => { p.hidden = p.dataset.view !== name; });
      try { localStorage.setItem("pc-view", name); } catch (e) { /* storage blocked */ }
    };
    $$("a", tabs).forEach(a => a.addEventListener("click", e => { e.preventDefault(); show(a.dataset.view); }));
    let saved = null;
    try { saved = localStorage.getItem("pc-view"); } catch (e) { /* storage blocked */ }
    show(location.hash === "#details" ? "details" : (saved === "details" ? "details" : "board"));
  });

  /* ---------- live search + sort for drives ---------- */
  const list = $("[data-filter-list]");
  if (list) {
    const q = $("#q"), sort = $("#sort"), count = $(".toolbar .count");
    const items = $$(".ticket", list);
    const apply = () => {
      const term = (q.value || "").trim().toLowerCase();
      let shown = 0;
      items.forEach(t => {
        const hit = !term || t.dataset.text.includes(term);
        t.hidden = !hit;
        if (hit) shown++;
      });
      const key = sort.value;
      const val = t => key === "pay" ? -parseFloat(t.dataset.pay)
                    : key === "deadline" ? parseFloat(t.dataset.days)
                    : -parseFloat(t.dataset.match || 0);
      items.slice().sort((a, b) => val(a) - val(b)).forEach(t => list.appendChild(t));
      if (count) count.textContent = shown + " of " + items.length + " drives";
    };
    q.addEventListener("input", apply);
    sort.addEventListener("change", apply);
    apply();
  }

  /* ---------- Ctrl/Cmd + K quick-jump palette ---------- */
  const links = $$(".tabs-inner a, .wordmark")
    .map(a => ({ label: a.textContent.trim().replace(/\s+/g, " ") || "Home", href: a.getAttribute("href") }))
    .filter((l, i, arr) => l.href && arr.findIndex(x => x.href === l.href) === i);
  $$("[data-palette]").forEach(a => links.push({ label: a.dataset.palette, href: a.getAttribute("href") }));
  if (links.length > 1 && typeof HTMLDialogElement === "function") {
    const dlg = document.createElement("dialog");
    dlg.className = "palette";
    dlg.innerHTML = '<input type="search" placeholder="Jump to a page..." aria-label="Jump to a page"><ul role="listbox"></ul>'
                  + '<div class="hint">Up / Down to move, Enter to open, Esc to close</div>';
    document.body.appendChild(dlg);
    const input = $("input", dlg), ul = $("ul", dlg);
    let idx = 0, current = [];
    const render = () => {
      const t = input.value.trim().toLowerCase();
      current = links.filter(l => l.label.toLowerCase().includes(t));
      idx = Math.min(idx, Math.max(0, current.length - 1));
      ul.textContent = "";
      current.forEach((l, i) => {
        const li = document.createElement("li");
        if (i === idx) li.className = "on";
        const a = document.createElement("a");
        a.href = l.href;
        a.textContent = l.label;
        const s = document.createElement("span");
        s.textContent = "go";
        a.appendChild(s);
        li.appendChild(a);
        ul.appendChild(li);
      });
    };
    const open = () => { input.value = ""; idx = 0; render(); dlg.showModal(); input.focus(); };
    input.addEventListener("input", () => { idx = 0; render(); });
    input.addEventListener("keydown", e => {
      if (e.key === "ArrowDown") { idx = Math.min(idx + 1, current.length - 1); render(); e.preventDefault(); }
      if (e.key === "ArrowUp") { idx = Math.max(idx - 1, 0); render(); e.preventDefault(); }
      if (e.key === "Enter" && current[idx]) location.href = current[idx].href;
    });
    dlg.addEventListener("click", e => { if (e.target === dlg) dlg.close(); });
    document.addEventListener("keydown", e => {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === "k") { e.preventDefault(); dlg.open ? dlg.close() : open(); }
    });
    $$(".palette-btn").forEach(b => { b.hidden = false; b.addEventListener("click", open); });
  }
})();
