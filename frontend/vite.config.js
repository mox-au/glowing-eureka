import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 3000,
    host: '0.0.0.0',
    allowedHosts: [
      'localhost',
      'dev.test-nook.com',
      '.test-nook.com'  // This allows any subdomain of test-nook.com
    ],
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});
