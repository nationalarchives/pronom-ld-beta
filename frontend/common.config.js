const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const AssetConfigWebpackPlugin = require('asset-config-webpack-plugin');
require("babel-polyfill");
const fs = require('fs');
const buildPath = path.resolve(__dirname, 'dist');
const SRC = path.resolve(__dirname, 'src');

const HTMLFiles = fs.readdirSync(SRC + '/base').map(file => {
  const [name, ext] = file.split('.');
  const localJSExists = fs.existsSync(SRC + '/js/pages/' + name + '.js');
  let chunks = ['common'];
  if (localJSExists) {
    chunks = [...chunks, name];
  }
  return {
    name,
    filename: file,
    template: SRC + '/base/' + file,
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
    new AssetConfigWebpackPlugin()
  ]
}