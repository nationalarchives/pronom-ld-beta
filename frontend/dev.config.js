const { merge } = require('webpack-merge');
const fs = require('fs');
const path = require('path');
const common = require('./common.config.js');
const SRC = path.resolve(__dirname, 'src');

const HTMLFiles = fs.readdirSync(SRC + '/base').filter(file => file.includes('.pug')).map(file => {
  const [name, ext] = file.split('.');
  return {
    from: new RegExp(`^\/${name}$`), to: `/${name}.html`,
  }
});

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
      rewrites:[
        { from: /^\/$/, to: '/index.html' },
        ...HTMLFiles,
      ]
    }
  }
});