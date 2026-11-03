import globals from "globals";

export default [{
  files: ["app/src/main/resources/static/**/*.js"],
  languageOptions: {
    ecmaVersion: 2022,
    sourceType: "script",
    globals: {
      ...globals.browser,
      $: "readonly",
      api: "readonly",
      escapeText: "readonly",
      role: "readonly",
      doctors: "writable",
      loadDoctors: "readonly",
      updateHeader: "readonly"
    }
  },
  rules: {
    "no-undef": "error",
    "no-unreachable": "error",
    "no-dupe-args": "error",
    "no-dupe-keys": "error",
    "no-duplicate-case": "error",
    "valid-typeof": "error",
    "no-invalid-regexp": "error"
  }
}];
