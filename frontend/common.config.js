const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const AssetConfigWebpackPlugin = require('asset-config-webpack-plugin');
const tooltips = require('./tooltips');
require("babel-polyfill");
const fs = require('fs');
const buildPath = path.resolve(__dirname, 'dist');
const SRC = path.resolve(__dirname, 'src');

const getTooltip = (type,id) => {
  return tooltips[type][id];
}

const HTMLFiles = fs.readdirSync(SRC + '/base').filter(file => file.includes('.pug')).map(file => {
  const [name, ext] = file.split('.');
  const localJSExists = fs.existsSync(SRC + '/js/pages/' + name + '.js');
  let chunks = ['common'];
  if (localJSExists) {
    chunks = [...chunks, name];
  }
  return {
    name,
    filename: `${name}.html`,
    template: SRC + '/base/' + file,
    getTooltip,
    inject: true,
    chunks,
    minify: {
      html5: true,
      collapseWhitespace: false,
      preserveLineBreaks: true,
      minifyCSS: true,
      minifyJS: true,
      removeComments: true,
    },
  }
});

module.exports = {
  entry: HTMLFiles.reduce((acc, next) => {
    acc[next.name] = `${SRC}/js/pages/${next.name}.js`;;
    return acc
  }, { 'babel-polyfill': 'babel-polyfill' }),
  output: {
    clean: true,
    path: buildPath,
    filename: '[name].js',
    publicPath: '/',
  },
  module: {
    rules: [
      {
        test: /\.pug$/,
        exclude: [/node_modules/],
        include: path.join(__dirname, 'src', 'base'),
        use: [{
          loader: 'pug-loader',
        }]
      },
      {
        test: /\.js$/,
        use: 'babel-loader',
        exclude: [/node_modules/],
      },
      {
        test: /\.scss$/i,
        use: [
          MiniCssExtractPlugin.loader,
          "css-loader",
          {
            loader: "postcss-loader",
            options: {
              postcssOptions: {
                plugins: { autoprefixer: {} }
              }
            }
          },
          "sass-loader"
        ]
      },
      {
        test: /\.(png|jpe?g|gif|svg|eot|ttf|woff|woff2)$/i,
        // More information here https://webpack.js.org/guides/asset-modules/
        type: "asset",
      },
    ]
  },
  plugins: [
    ...HTMLFiles.map(conf => new HtmlWebpackPlugin(conf)),
    new MiniCssExtractPlugin({ filename: "[name].css" }),
    new AssetConfigWebpackPlugin(),
    new webpack.ProvidePlugin({
      $: 'jquery',
      jQuery: 'jquery',
    })
  ]
}