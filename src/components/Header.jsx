import { BellIcon } from "lucide-react";

const Header = ({ userName }) => {
  const userInitials = userName
    .split(" ")
    .map((name) => name[0])
    .join("")
    .toUpperCase();

  return (
    <header className="bg-[#0F172A] text-white flex justify-between items-center p-4 shadow-md">
      {/* Logo */}
      <div className="text-2xl font-bold tracking-wide">
        <img src="/logo1.jpg" alt="App Logo" className="h-10 w-10 inline-block" />
        TradeNova
      </div>

      {/* Notification + User Icon */}
      <div className="flex items-center gap-4">
        {/* Notification Icon */}
        <button className="relative p-2 rounded-full hover:bg-[#1E293B] transition">
          <BellIcon size={24} />
        </button>

        {/* User Initials Icon */}
        <button
          onClick={() => console.log("Go to user settings")}
          className="h-10 w-10 flex items-center justify-center rounded-full bg-blue-500 text-white font-semibold"
        >
          {userInitials}
        </button>
      </div>
    </header>
  );
};

export default Header;