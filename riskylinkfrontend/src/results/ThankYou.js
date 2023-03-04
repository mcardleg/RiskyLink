import logo from '../logo.png';
import './ThankYou.css';

function ThankYou() {
    return (
        <div className="ThankYou">
          <header className="ThankYou-header">
            <img src={logo} className="logo" alt="logo" />
            <br></br>
            <p>Thank you for using RiskyLink!</p>
            <br></br>
            <p>Please consider what you have learned while using this tool when publishing your dataset.</p>
          </header>
        </div>
      );
}

export default ThankYou;