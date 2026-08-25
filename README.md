# OpenNest Horizon Planner

**Interactive Retirement, Income & Tax Optimizer**

A powerful, fully client-side retirement planning tool. Model your entire financial future — accounts, Social Security, Roth conversions, withdrawal strategies, long-term care, state taxes, Coast FI, and more — with nothing ever leaving your browser.

---

## Live Demo

Once you enable GitHub Pages (see below), the planner will be available at:

```
https://kingfish600.github.io/opennest-horizon-planner/
```

Or simply open `index.html` in any modern browser.

---

## Key Features

### Core Planning
- Multi-account modeling (Cash, Taxable, Pre-Tax / 401k / tIRA, Roth, HSA)
- Full cash-flow waterfall with customizable withdrawal order
- Inflation, growth rates, dividend yield, and dynamic bond-tent glidepath
- Real vs nominal views and probability cones (Monte Carlo)

### Income & Claiming
- Social Security claiming optimizer (62–70) with break-even matrix and opportunity cost
- Pension modeling + lump-sum buyout analyzer
- Phased retirement / bridge income
- Guaranteed income floor & annuity modeler (SPIA / DIA)
- QLAC support

### Tax Optimization
- Roth conversion ladder with tax-bracket top-off
- 0% capital-gains harvesting
- Tax-loss harvesting & carryforward
- Charitable bunching / Donor-Advised Fund (DAF)
- Qualified Charitable Distributions (QCD)
- TCJA sunset scenario
- State income tax & relocation modeler
- IRMAA / Medicare surcharge modeling + SSA-44 work-stoppage waiver
- ACA Premium Tax Credit (pre-65 marketplace) modeler

### Withdrawal Strategies
- Fixed Safe Withdrawal Rate (Bengen / Trinity)
- Guyton-Klinger Guardrails
- Variable Percentage Withdrawal (VPW)
- CAPE valuation adjuster (Shiller)
- Tax-efficient withdrawal sequencing (Classic / Proportional / Roth-first)

### Advanced Modules
- **3-Bucket Engine** — cash buffer / fixed income / growth equity with automatic refill logic
- Long-Term Care (LTC) stress test
- Real-estate downsizing event with §121 exclusion
- Mortgage payoff vs. invest arbitrage
- HECM reverse-mortgage volatility buffer
- HSA “shoebox” delayed-reimbursement strategy
- IRS Rule 72(t) SEPP early-withdrawal program
- Retirement spending smile (go-go / slow-go / no-go)
- One-off bucket-list expenses
- Spouse survivor / widow’s penalty stress test
- Historical crisis sequence backtester (Depression, Stagflation, Dot-com, GFC, etc.)
- Sequence-of-Returns Risk isolator
- Retirement age × spending viability heatmap
- Coast FI / FIRE analytics (SharpeMONEY Coast FI Framework)
- “Rich, Broke or Dead?” longevity & mortality visualizer
- Estate & legacy modeling (SECURE Act 2.0 heir rules)

### Usability
- Plan A / Plan B scenario comparison
- Year-by-year ledger with full detail
- Export PDF executive summary
- Export / import JSON snapshots
- Fully responsive (desktop + mobile)
- 100% local — all calculations run in your browser

---

## Privacy

**Nothing leaves your browser.**  
All data, calculations, and Monte Carlo simulations run entirely client-side. No accounts, no tracking, no server.

---

## How to Use

1. Open `index.html` in Chrome, Firefox, Edge, or Safari.
2. Start on the **Dashboard**.
3. Adjust balances, ages, spending, and strategies in the **Accounts & Cash Flow**, **Timeline**, and other tabs.
4. Everything recalculates live.
5. Use **Scenario Compare** to test alternatives side-by-side.
6. Export a printable PDF summary when ready.

Your inputs are automatically saved in the browser’s local storage.

---

## Download for Offline Use

* **Desktop / Web Browser:** [Download index.html (Right-click -> Save Link As)](https://raw.githubusercontent.com/kingfish600/opennest-horizon-planner/main/index.html)
* **Android Device:** [Download Standalone APK (v1.3.0)](https://github.com/kingfish600/opennest-horizon-planner/releases/download/v1.3.0/opennest_horizon_planner.apk) · or the always-current build: [opennest_horizon_planner.apk in-repo](https://raw.githubusercontent.com/kingfish600/opennest-horizon-planner/main/opennest_horizon_planner.apk)
---

## Android App (v1.3.0)

A native, fully offline Android wrapper ships in this repo ([`android/`](android/)) and as a prebuilt APK above.

* Loads the entire planner from bundled assets — **no INTERNET permission**, works in airplane mode
* localStorage / DOM storage persist your plans between launches
* Exports (JSON plan snapshots, ledger CSVs) save straight to your device **Downloads** folder
* Import snapshots via the native file picker; Print Report opens the system print dialog with "Save as PDF"
* Hardware-accelerated WebView for fluid Chart.js rendering; back button walks in-app history
* Fat-finger-friendly bottom tab bar on phones

Build it yourself:

```
cd android
./gradlew assembleDebug   # requires JDK 17 + Android SDK (SDK 34)
# → app/build/outputs/apk/debug/app-debug.apk
```

---

## What's New

### v1.3.0
* Print Report now exports a real PDF via the Android system print dialog ("Save as PDF")
* JSON plan snapshots and ledger CSV exports land in device Downloads (native bridge — no more silently swallowed downloads)
* Snapshot import wired to the native file picker
* Fixed horizontal side-scroll inside Accounts & Cash Flow cards on tablets (ACA grid reflow + overflow hardening)
* ~50% taller bottom tab bar touch targets on phones

---

## Acknowledgments & Inspirations
* Longevity & Mortality Visualizer inspired by Engaging Data's classic "Rich, Broke or Dead" model.
* Safe withdrawal rate and guardrail concepts built on Bengen, Guyton-Klinger, and Trinity study research.

---

## Deploying on GitHub Pages

1. Create a new public repository on GitHub.
2. Push this folder to the `main` branch.
3. Go to **Settings → Pages**.
4. Source: **Deploy from a branch** → Branch: `main` → Folder: `/ (root)`.
5. Save. After a minute or two your planner will be live.

---

## License

MIT License — see [LICENSE](LICENSE).

You are free to use, modify, share, and build upon this tool.

---

## Disclaimer

This is an educational planning tool, **not financial, tax, or legal advice**.  
Tax rules, Social Security, Medicare, and investment returns change. Always consult a qualified professional before making major financial decisions.

Past performance and historical sequences do not guarantee future results. Monte Carlo results are illustrative only.
