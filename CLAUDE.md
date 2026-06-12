# CLAUDE.md — admission-system

## Project Context
研究生招生信息管理系统 (Graduate Admission Information Management System)
- Backend: Spring Boot 2.7.15 + MyBatis-Plus 3.5.3.1 + MySQL 8.0, Java 11
- Frontend: Vue 3 + Vite + Element Plus + Axios + Vue Router 4
- Database: admission_db (MySQL)

## Gstack Skills & Commands

This project uses [gstack](https://github.com/garrytan/gstack) — Garry Tan's Claude Code workflow system.
All skills are installed at `~/.claude/skills/gstack/`.

### Planning Phase
| Skill | Description |
|---|---|
| `/office-hours` | YC Partner — 6 forcing questions to refine product ideas before coding |
| `/plan-ceo-review` | CEO/Founder — first-principles product thinking, scope decisions |
| `/plan-eng-review` | Eng Manager — architecture, data flow, edge cases, test planning |
| `/plan-design-review` | Senior Designer — UI/UX evaluation, AI slop detection |
| `/plan-devex-review` | DevEx review — developer experience considerations |
| `/plan-tune` | Tune an existing plan with additional context |

### Design Phase
| Skill | Description |
|---|---|
| `/design` | Design creation and iteration |
| `/design-consultation` | Design consultation and feedback |
| `/design-review` | Design review and critique |
| `/design-html` | HTML/CSS design implementation |
| `/design-shotgun` | Rapid design exploration |
| `/ios-design-review` | iOS-specific design review |

### Build & Implementation
| Skill | Description |
|---|---|
| `/autoplan` | Autonomous planning — generate plan from idea automatically |
| `/pair-agent` | Pair programming with a subagent |
| `/spec` | Specification-driven development |
| `/skillify` | Convert a workflow into a reusable skill |
| `/test` | Test generation and execution |
| `/learn` | Learn from codebase patterns |

### Review Phase
| Skill | Description |
|---|---|
| `/review` | Staff Engineer — finds production bugs, auto-fixes obvious ones |
| `/investigate` | Systematic root-cause debugging |
| `/devex-review` | Developer experience review |

### QA & Testing
| Skill | Description |
|---|---|
| `/qa` | QA Lead — browser testing with real Chromium, finds & fixes bugs |
| `/qa-only` | Report-only QA (no fixes applied) |
| `/ios-qa` | iOS-specific QA testing |
| `/ios-fix` | iOS-specific bug fixes |
| `/ios-sync` | iOS project synchronization |

### Security
| Skill | Description |
|---|---|
| `/cso` | Chief Security Officer — OWASP Top 10 + STRIDE threat modeling |
| `/guard` | Security guard rails and policy enforcement |

### Ship & Deploy
| Skill | Description |
|---|---|
| `/ship` | Release Engineer — syncs, tests, pushes, opens PR |
| `/land-and-deploy` | Deployment Engineer — merges PR, waits for CI/deploy, verifies production |
| `/setup-deploy` | Configure deployment settings |
| `/landing-report` | Generate landing/deployment report |

### Browser & Web
| Skill | Description |
|---|---|
| `/browse` | **REQUIRED for all web browsing** — Real Chromium browser automation (~100ms per command) |
| `/open-gstack-browser` | Open gstack browser session |
| `/scrape` | Web scraping with browser automation |
| `/setup-browser-cookies` | Configure browser authentication cookies |

### Documentation
| Skill | Description |
|---|---|
| `/document-generate` | Technical documentation generation |
| `/document-release` | Auto-update docs to match shipped code |
| `/make-pdf` | Generate PDF from documentation |

### Operations & Meta
| Skill | Description |
|---|---|
| `/retro` | Eng Manager — weekly retrospectives, shipping streaks, test health |
| `/context-save` | Save current context for restoration |
| `/context-restore` | Restore previously saved context |
| `/freeze` | Freeze current state |
| `/unfreeze` | Unfreeze and restore state |
| `/health` | Health check and diagnostics |
| `/gstack-upgrade` | Upgrade gstack to latest version |
| `/careful` | Careful/cautious mode for sensitive operations |
| `/canary` | Canary testing and feature flags |

### Multi-Model
| Skill | Description |
|---|---|
| `/codex` | Second Opinion — cross-model review via OpenAI Codex CLI |
| `/benchmark` | Run benchmarks against current implementation |
| `/benchmark-models` | Compare performance across multiple models |
| `/model-overlays` | Configure model-specific behavior overlays |

### Infrastructure
| Skill | Description |
|---|---|
| `/supabase` | Supabase database operations |
| `/setup-gbrain` | Set up gbrain (knowledge base integration) |
| `/sync-gbrain` | Sync with gbrain knowledge base |

---

## Rules
1. **All web browsing MUST use `/browse` skill** — never use WebFetch/WebSearch directly when `/browse` is available. `/browse` provides real Chromium browser automation for accurate results.
2. Before any significant feature, run `/office-hours` → `/plan-ceo-review` → `/plan-eng-review`
3. Before shipping, run `/review` → `/qa` → `/ship`
4. After shipping, run `/retro`
5. For security-sensitive changes, run `/cso`

## OpenSpec Integration
This project uses [OpenSpec](https://github.com/Fission-AI/OpenSpec) for spec-driven development.
- `/opsx:propose` — Create a change proposal
- `/opsx:apply` — Implement an approved change
- `/opsx:archive` — Archive a completed change
- `/opsx:explore` — Explore specs and changes
- `/opsx:sync` — Sync specs with implementation

## Superpowers Integration
Core skills library installed at `~/.claude/skills/superpowers/` providing:
- `/brainstorm` — Interactive design refinement
- `/write-plan` — Create implementation plan
- `/execute-plan` — Execute plan in batches
- TDD, systematic debugging, code review, subagent collaboration workflows
