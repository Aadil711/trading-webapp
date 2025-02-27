const Button = ({ children, onClick }) => {
    return (
      <button
        onClick={onClick}
        style = {{backgroundColor: "#125398"}}
        className="w-full p-3 rounded-2xl text-white text-xl font-bold tracking-wide hover:bg-blue-700 transition duration-300"
      >
        {children}
      </button>
    );
  };
  
  export default Button;
