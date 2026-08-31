// lighthouserc.js
// Umbrales minimos de Lighthouse CI para SGROAS (Bloque C.5 de la guia).
// Uso: npx lhci autorun   (construye, sirve y audita el frontend)

const URL = process.env.LHCI_URL || 'http://localhost:4200';

module.exports = {
  ci: {
    collect: {
      url: [URL],
      numberOfRuns: 1,
      settings: {
        preset: 'mobile',
        throttling: {
          rttMs: 150,
          throughputKbps: 1600,
          cpuSlowdownMultiplier: 4,
        },
      },
    },
    assert: {
      assertions: {
        'categories:performance': ['error', { minScore: 0.8 }],
        'categories:accessibility': ['error', { minScore: 0.9 }],
        'categories:best-practices': ['error', { minScore: 0.9 }],
        'categories:seo': ['error', { minScore: 0.9 }],
      },
    },
    upload: {
      target: 'filesystem',
      outputDir: 'docs/mediciones/lighthouse',
    },
  },
};
