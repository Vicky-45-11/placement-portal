# Put the Placement Portal online

This app is Java (Servlets/JSP on Tomcat) plus MySQL, so it needs a host that runs a server and a database.
**Vercel cannot run it**: Vercel only hosts static sites and short JavaScript/Python functions.

The repo includes a `Dockerfile`. It builds the WAR, runs it on Tomcat 10.1, listens on the host's `$PORT`,
and creates the tables and sample data automatically the first time it meets an empty database.

## Option A: Railway (recommended, app + MySQL in one place)

1. Sign in at https://railway.com with GitHub.
2. **New Project → Deploy from GitHub repo →** pick `Vicky-45-11/placement-portal`.
   Railway sees the `Dockerfile` and builds it.
3. In the same project, click **+ Create → Database → MySQL**.
4. Open the app service → **Variables → + New Variable → Add Reference**, and add these five from the MySQL service:
   `MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`.
5. The app redeploys. On first start it creates every table and the sample data.
6. App service → **Settings → Networking → Generate Domain**. That `*.up.railway.app` link is your public website.

Log in with `admin@college.edu` / `admin123`, then **change that password** from the Password tab,
because the sample passwords are public in this repo.

## Option B: Render (free web service) + a free MySQL elsewhere

Render has no MySQL, so create one first, for example a free MySQL service on https://aiven.io.

1. Create the MySQL database and copy its host, port, user, password and database name.
2. On https://render.com: **New → Web Service →** connect `Vicky-45-11/placement-portal`. Render detects the Dockerfile.
3. Add environment variables:
   - `DB_URL` = `jdbc:mysql://HOST:PORT/DBNAME?sslMode=REQUIRED&connectionTimeZone=SERVER`
   - `DB_USER`, `DB_PASSWORD`
4. Deploy. The free instance sleeps after 15 minutes idle, so the first visit after a break takes about a minute.

## Settings the app reads

| Variable | Meaning |
|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | JDBC connection. Wins over everything else. |
| `MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE` | Used automatically when `DB_URL` is not set (Railway's names). |
| `DB_AUTO_INIT` | `true` in the Docker image: creates tables + sample data if the database is empty. Never touches existing data. |
| `UPLOAD_DIR` | Where resume PDFs are saved. Container disks are wiped on redeploy, so uploaded resumes do not survive a redeploy on free hosting. |
| `PORT` | Port to listen on, set by the host. Defaults to 8080. |

## Run the container yourself

```bash
docker build -t placement-portal .
docker run -p 8080:8080 -e DB_URL="jdbc:mysql://host.docker.internal:3306/placement_db?allowPublicKeyRetrieval=true&useSSL=false" \
  -e DB_USER=placement_app -e DB_PASSWORD=change_me placement-portal
```
