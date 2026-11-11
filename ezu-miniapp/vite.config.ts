import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [uni()],
  server: {
    proxy: {
      // H5 开发模式：/api 转发到本地后端（仅 dev:h5 生效）
      "/api": {
        target: "http://localhost:9000",
        changeOrigin: true,
      },
    },
  },
});
