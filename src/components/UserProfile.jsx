const UserProfile = ({ name, initials }) => {
    return (
      <div className="flex flex-col items-center mb-8">
        <div className="w-24 h-24 rounded-full bg-gray-700 flex items-center justify-center text-white text-3xl">
          {initials}
        </div>
        <h2 className="text-2xl font-bold text-gray-200 mt-4">{name}</h2>
      </div>
    );
  };
  
  export default UserProfile;