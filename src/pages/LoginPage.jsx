import { useState } from "react";
import UserProfile from "../components/UserProfile";
import InputField from "../components/InputField";
import Button from "../components/Button";
import { Link } from "react-router-dom";

const LoginPage = () => {
    const [pin, setPin] = useState("");
  
    const handleSubmit = (e) => {
      e.preventDefault();
      console.log("Logging in with PIN:", pin);
    };
  
    return (
      <div className="flex min-h-screen">
        {/* Left partition for logo */}
        <div className="w-1/2 flex justify-center items-center bg-gradient-to-br from-blue-100 to-blue-300">
          <div>
          <img
    src="logo2.png"
    alt="Trading Company Logo"
    className="w-full h-auto max-w-xl md:max-w-2xl lg:max-w-3xl"
/>

          </div>
        </div>
  
        {/* Right partition for login box */}
        <div className="w-1/2 flex justify-center items-center bg-gradient-to-br from-gray-900 to-black">
          <div className="bg-gray-800 bg-opacity-60 backdrop-blur-lg p-10 rounded-3xl shadow-2xl w-96">
            {/* UserProfile component */}
            <UserProfile name="Chris George" initials="CG" />
  
            <form onSubmit={handleSubmit}>
              {/* InputField component */}
              <InputField
                label="Enter PIN"
                type="password"
                value={pin}
                onChange={(e) => setPin(e.target.value)}
              />
  
              {/* Button component */}
              <Button>Login</Button>
  
              {/* Signup link */}
              <div className="text-center mt-4">
                <p className="text-gray-400">
                  New user?{" "}
                  <Link to="/signup" className="text-blue-400 hover:underline">
                    Sign up!
                  </Link>
                </p>
              </div>
            </form>
          </div>
        </div>
      </div>
    );
  };
  
  export default LoginPage;
