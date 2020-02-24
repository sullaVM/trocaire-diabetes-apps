# Trocaire Diabetes App Backend

## /src Structure Guide

### firebase

All Firebase-related code including user creation, session cookie creation, revoking cookies, and admin and doctor checks are in here.

### private

Private files for the admin web interface.

### public

Public static files that supports the admin web interface.

### roles

- admin.ts
  - Actions only accedible by admins.
- doctor.ts
  - Database queries only accessible by doctors.
- patient.ts
  - Database queires accessible by patients and doctors.

### database.ts

Database code.

### index.ts

Main server code.
