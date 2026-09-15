# AI Usage

This document discloses how AI tools were used during the development of this
assignment, in line with the course's academic integrity policy.


## Tools used
I used Claude (Anthropic) for this assignment.

## How AI was used
AI was used as a learning and debugging aid, not to generate the solution wholesale.
Specifically:

- **Understanding concepts** — explaining Spring Boot features (dependency injection,
  `@RestControllerAdvice`, virtual threads) and Java concurrency (`LongAdder`,
  `CountDownLatch`) that were new to me.
- **Debugging** — interpreting error messages and stack traces (e.g. bean injection
  errors, Maven build failures) to understand what was going wrong.
- **Guidance on structure and testing** — discussing how to organise packages, name
  classes, and approach testing. For example, using an ApplicationTerminator interface
  to make graceful shutdown testable.
- **Frontend (csstest.css & app.js)** — improving the UI, adding animations, and structuring the
  client-side JavaScript for recording, uploading, and displaying results.

## What I did myself
- Wrote and understand all the code in this submission.
- Made the design decisions (documented in ARCHITECTURE.md) and can explain the
  reasoning behind each.
- Debugged and fixed issues through the incremental commits in the Git history.
- Wrote the tests and understand what each one verifies.

## Verification of understanding
I understand how each component works and could explain or modify any part of this
codebase without assistance.