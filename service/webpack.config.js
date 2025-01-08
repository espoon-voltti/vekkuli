const path = require('path');

module.exports = {
  entry: './index.js',
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'src/main/resources/public/virkailija/static'),
  },
  mode: 'production',
};
