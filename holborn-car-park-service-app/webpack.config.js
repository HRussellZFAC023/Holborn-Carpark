const HtmlWebPackPlugin = require("html-webpack-plugin");

module.exports = {
  entry: './server/react/manager.js',
  output: {
    path: __dirname + '/public/protected/react',
    filename: 'manager.js'
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
      template: "./server/react/manager.html",
      filename: "./manager.html"
    })
  ]
};
