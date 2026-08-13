import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';

// SPA autónoma en frontend/. En dev, el proxy reenvía /api y /images al
// backend Spring Boot (localhost:8080) para evitar CORS. El resto de
// recursos (JS/CSS de la SPA) los sirve Vite.
export default defineConfig({
  plugins: [svelte()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/images': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});
