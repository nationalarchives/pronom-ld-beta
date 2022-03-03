const { merge } = require('webpack-merge');
const common = require('./common.config.js');

module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  devServer: {
    static: 'src/base',
    port: 8080,
    magicHtml: true,
  }
});