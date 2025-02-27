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
    <div
      className="flex justify-center items-center min-h-screen"
      style={{
        backgroundImage: "url('/background.jpeg')", // You forgot the 'url()' part here
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      {/* Centered transparent login box */}
      <div className="bg-gray-800 bg-opacity-70 backdrop-blur-md p-10 rounded-3xl shadow-2xl w-96">
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
  );
};

export default LoginPage;
