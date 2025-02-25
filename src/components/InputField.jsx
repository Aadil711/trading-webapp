import { useState } from "react";
import { Eye, EyeOff } from "lucide-react"; // Using lucide-react for beautiful icons

const InputField = ({ label, value, onChange }) => {
  const [showPin, setShowPin] = useState(false); // State to toggle visibility

  const toggleVisibility = () => setShowPin(!showPin);

  return (
    <div className="relative w-full mb-6"> {/* Space added */}
      <label className="block text-white text-lg mb-2">{label}</label>
      <div className="relative">
        <input
          type={showPin ? "text" : "password"} // Toggle between text and password
          className="w-full p-3 rounded-2xl bg-[#2563EB] text-white text-center text-xl tracking-widest shadow-lg placeholder-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400"
          value={value}
          onChange={onChange}
          required
        />
        {/* Eye icon for toggling */}
        <button
          type="button"
          onClick={toggleVisibility}
          className="absolute right-3 top-1/2 transform -translate-y-1/2 text-blue-400" // Blue icon
        >
          {showPin ? <EyeOff size={24} /> : <Eye size={24} />}
        </button>
      </div>
    </div>
  );
};

export default InputField;