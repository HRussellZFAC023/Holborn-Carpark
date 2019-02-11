const HtmlWebPackPlugin = require("html-webpack-plugin");

module.exports = {
  entry: './server/react/index.js',
  output: {
    path: __dirname + '/public/protected/react',
    filename: 'index.js'
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader"
        }
      },
      {
        test: /\.html$/,
        use: [
          {
            loader: "html-loader"
          }
        ]
      }
    ]
  },
  plugins: [
    new HtmlWebPackPlugin({
      template: "./server/react/index.html",
      filename: "./index.html"
    })
  ]
};