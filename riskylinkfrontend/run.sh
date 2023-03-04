cd riskylinkfrontend
sudo apt update
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.38.0/install.sh | bash
source ~/.bashrc
nvm install v16.0.0
npm install
chmod +x node_modules/.bin/react-scripts
# webpack.config.js edits
npm run build
npm install -g serve
serve -s build