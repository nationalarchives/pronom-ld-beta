const path = require('path');
const { merge } = require('webpack-merge');
const prod = require('./prod.config.js');
const resourcesDir = path.resolve(__dirname, '../backend/src/main/resources');
const FileManagerPlugin = require('filemanager-webpack-plugin');

// Same as Production build but outputs to the backend project so the JAR can be built
module.exports = merge(prod, {
  plugins: [
    new FileManagerPlugin({
      events: {
        onEnd: [
          {
            delete: [
              { source: `${resourcesDir}/static`, options: { force: true } },
              { source: `${resourcesDir}/templates`, options: { force: true } }
            ]
          },
          { mkdir: [`${resourcesDir}/static/media`, `${resourcesDir}/templates`] },
          {
            copy: [
              { source: `./dist/*.!(html)`, destination: `${resourcesDir}/static/` },
              { source: `./dist/static/media/*`, destination: `${resourcesDir}/static/media/` },
              { source: `./dist/*.html`, destination: `${resourcesDir}/templates/` },
            ]
          },
        ],
      },
    })
  ]
});

