// Fix Node core modules for sql.js in Webpack 5

config.resolve = config.resolve || {};
config.resolve.fallback = {
    ...(config.resolve.fallback || {}),
    fs: false,
    path: false,
    crypto: false
};

const CopyWebpackPlugin = require('copy-webpack-plugin');

config.plugins = config.plugins || [];
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            {
                from: require.resolve('sql.js/dist/sql-wasm.wasm'),
                to: '.'
            }
        ]
    })
);