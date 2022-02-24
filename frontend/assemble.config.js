const path = require('path');
const { merge } = require('webpack-merge');
const prod = require('./prod.config.js');
const buildPath = path.resolve(__dirname, '../backend/src/main/resources/templates/');

// Same as Production build but outputs to the backend project so the JAR can be built
module.exports = merge(prod, {
  output: {
    path: buildPath,
  }
});