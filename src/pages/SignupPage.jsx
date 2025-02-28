import { useState } from "react";
import InputField from "../components/InputField";
import Button from "../components/Button";
import { Link } from "react-router-dom";

const SignupPage = () => {
  const [email, setEmail] = useState("");
  const [pin, setPin] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Signing up with:", email, pin);
  };

  return (
    <div
      className="flex justify-center items-center min-h-screen"
      style={{
        backgroundImage: "url('/background.png')",
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      <div className="bg-gray-800 bg-opacity-70 backdrop-blur-md p-10 rounded-3xl shadow-2xl w-96">
        <h2 className="text-3xl text-white text-center mb-6">Sign Up</h2>

        <form onSubmit={handleSubmit}>
          <InputField
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <InputField
            label="Create PIN"
            type="password"
            value={pin}
            onChange={(e) => setPin(e.target.value)}
          />
          <Button>Sign Up</Button>

          <div className="text-center mt-4">
            <p className="text-gray-400">
              Already have an account?{" "}
              <Link to="/" className="text-blue-400 hover:underline">
                Login
              </Link>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SignupPage;
