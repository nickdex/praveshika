/** @type {import('tailwindcss').Config} */
module.exports = {
  content: process.env.NODE_ENV === "production" ? ["./public/js/main.js"] : ["./public/index.html", "src/**/*.cljs"],
  theme: {
    extend: {},
  },
  plugins: [
    require('@tailwindcss/forms')
  ],
}