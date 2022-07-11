const { merge } = require('webpack-merge');
const fs = require('fs');
const path = require('path');
const common = require('./common.config.js');
const SRC = path.resolve(__dirname, 'src');

// This can be used for automatic endpoint mapping of {name}.pug files to a /{name} route. For now we use the hardcoded redirection map below
const HTMLFiles = fs.readdirSync(SRC + '/base').filter(file => file.includes('.pug')).map(file => {
  const [name, ext] = file.split('.');
  return {
    from: new RegExp(`^\/${name}$`), to: `/${name}.html`,
  }
});

// Redirection map, based on the endpoint map decribed in the README.md
const rewrites = [
  { from: /^\/$/, to: '/index.html' },
  { from: new RegExp("^\/search$"), to: "/search.html" },
  { from: new RegExp("^\/about$"), to: "/about.html" },
  { from: new RegExp("^\/faq$"), to: "/faq.html" },
  { from: new RegExp("^\/external-projects$"), to: "/external.html" },
  { from: new RegExp("^\/contact$"), to: "/contact.html" },
  { from: new RegExp("^\/contribute$"), to: "/contribute.html" },
  { from: new RegExp("^\/contribute\/form$"), to: "/form-choice.html" },
  { from: new RegExp("^\/contribute\/form\/new$"), to: "/user-form.html" },
  { from: new RegExp("^\/contribute/form\/(x-)?fmt\/\d+$"), to: "/user-form.html" },
  { from: new RegExp("^\/release-notes$"), to: "/rel-notes-list.html" },
  { from: new RegExp("^\/release-notes\/\d+$"), to: "/rel-notes-single.html" },
  { from: new RegExp("^\/droid"), to: "/droid.html" },
  { from: new RegExp("^\/vendor\/\d+$"), to: "/vendor.html" },
  { from: new RegExp("^\/(x-)?fmt\/\d+$"), to: "/file-format.html" },
  { from: new RegExp("^\/(?:x-)?(?:chr|sfw|cmp)\/\d+$"), to: "/generic-puid.html" },
  // internal interface
  { from: new RegExp("^\/dashboard$"), to: "/dashboard.html" },
  { from: new RegExp("^\/editorial$"), to: "/dashboard.html" },
  { from: new RegExp("^\/internal-search$"), to: "/internal-search.html" },
  { from: new RegExp("^\//editorial/content$"), to: "/content-manager.html" },
  { from: new RegExp("^\/content-manager$"), to: "/content-manager.html" },
  { from: new RegExp("^\/internal-form$"), to: "/internal-form.html" },
]

module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  devServer: {
    static: 'src/base',
    port: 8080,
    magicHtml: true,
    client: {
      logging: 'verbose',
    },
    historyApiFallback: {
      rewrites,
    }
  }
});