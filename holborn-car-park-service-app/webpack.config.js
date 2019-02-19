const HtmlWebPackPlugin = require("html-webpack-plugin");

module.exports = {
  entry: './server/react/manager.js',
  output: {
    path: __dirname + '/public/js',
    filename: 'manager.js',
    publicPath: ''
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
  resolve: {
    extensions: ['.js', '.jsx']
  },
  plugins: [
    new HtmlWebPackPlugin({
      template: "./server/react/manager.html",
      filename: "../protected/HTML/manager.html"
    })
  ]
};
