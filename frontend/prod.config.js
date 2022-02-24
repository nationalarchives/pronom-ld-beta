const path = require('path');
const { merge } = require('webpack-merge');
const TerserPlugin = require('terser-webpack-plugin');
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const common = require('./common.config.js');
const buildPath = path.resolve(__dirname, 'dist');

module.exports = merge(common, {
  mode: 'production',
  devtool: 'source-map',
  output: {
    path: buildPath,
    filename: '[name].[hash:20].js',
    publicPath: '/'
  },
  optimization: {
    minimize: true,
    minimizer: [
        new TerserPlugin({ parallel: true }),
        new OptimizeCssAssetsPlugin({})
    ]
}
});