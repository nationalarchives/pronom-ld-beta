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
  { from: new RegExp("^\/(x-)?fmt\/\d+$"), to: "/file-format.html" },
  { from: new RegExp("^\/(x-)?sfw\/\d+$"), to: "/generic-puid.html" }, // here we're using sfw/\d+ as a catch-all for all puid-based pages that are not file formats. the real system will also handle hardware, compression...etc
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