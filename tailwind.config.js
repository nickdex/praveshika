/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./public/index.html", "src/**/*.cljs"],
  theme: {
    extend: {},
  },
  plugins: [
    require('@tailwindcss/forms')
  ],
}