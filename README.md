# Smart Clinic Management System

IBM Java Developer capstone using Spring Boot 3.4.4, Java 17, MySQL and MongoDB. This is a local course demonstration: **every account, contact detail, appointment and prescription is fictional. Do not enter real patient data.**

## Run

Install Docker Desktop with Linux containers and Python 3, then run:

```sh
python scripts/init_env.py
docker compose up -d --build
```

Open http://127.0.0.1:8088. The first build runs Maven tests. Stop with `docker compose down`; data remains in Docker volumes. MySQL and MongoDB expose no host ports; the application binds only to loopback.

The initialization script creates random database/JWT secrets in ignored `.env` and refuses to overwrite it. The following public demo accounts all use `DemoPassword1!`:

| Role | Identifier |
|---|---|
| Administrator | admin |
| Doctor | avery.chen@example.test |
| Patient | patient1@example.test |

The empty database receives three doctors, five patients and five appointments for the following day. On later runs, select the original appointment date in the calendar. MySQL initializes reporting procedures on first startup.

## Implementation

- Public doctor search by name, specialty and morning/afternoon availability.
- Administrator login, doctor creation/deletion and SQL reporting.
- Patient registration/login, one-hour booking, cancellation and own records.
- Doctor calendar, profile, unavailable dates, completion and MongoDB prescriptions.
- BCrypt hashes; signed expiring JWTs; role and ownership checks; validated bodies; database locks and unique slots prevent double booking. Passwords are write-only JSON fields.
- Controllers, services, repositories and models: `app/src/main/java/com/project/back_end`.
- Active frontend: `static/index.html`, `static/clinic.js`, `static/clinic.css`, page-specific styles under `static/assets/css/`, reusable header/card scripts under `static/js/components/`, role dashboards under `templates/`.
- [Architecture](schema-architecture.md), [schema](schema-design.md), [user stories](user_stories.md).

The course frontend keeps `userRole` and `token` in localStorage; these are presentation state, never authorization proof. REST requests use Bearer headers. A matching HttpOnly SameSite cookie protects MVC dashboard navigation. Sign-out clears browser token storage and the cookie; existing JWTs remain valid until expiry. This demonstration uses local HTTP, Hibernate schema updates and standalone MongoDB. A production system would require TLS, migrations, managed secrets, database authentication, revocation, audit controls and clinical/privacy review.

## Validation

```sh
mvn -B -f app/pom.xml verify
# Or build/test with containerized Maven:
docker compose build clinic
# Against the running demo:
python scripts/smoke_test.py
```

On 2026-11-03, all 12 Maven tests passed during `docker compose up -d --build`. Live HTTP smoke checks passed against MySQL and MongoDB: all roles, missing/tampered/wrong-role tokens, password exclusion, doctor filtering, patient ownership, prescription persistence and simultaneous booking. DTO display fields and the parameterized case-insensitive doctor query also have focused tests. The smoke test creates a clearly fictional prescription and creates/cancels a test appointment. GitHub Actions repeats Maven verification and image building on push and pull request.

`scripts/capture_evidence.py` saves SQL and curl output without printing login tokens or database secrets. Dates derive from actual seeded appointments.

## Extending

Implement new rules in services, expose thin controller methods, and add repository queries and focused tests as needed. Keep ownership and locking in services so every client receives the same protection.

## Attribution

Based on the [IBM Skills Network starter](https://github.com/ibm-developer-skills-network/okssu-java-database-capstone-template), Apache-2.0 license retained. Implementation prepared with AI assistance for this course exercise.
