import { useState } from "react";
import Header from "../components/Header";

const companies = [
  { name: "Apple Inc.", logo: "/apple-logo.png" },
  { name: "Microsoft", logo: "/microsoft-logo.png" },
  { name: "Amazon", logo: "/amazon-logo.png" },
  { name: "Tesla", logo: "/tesla-logo.png" },
  { name: "Google", logo: "/google-logo.png" },
];

const PortfolioPage = () => {
  const [searchTerm, setSearchTerm] = useState("");

  const filteredCompanies = companies.filter((company) =>
    company.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div
      style={{ backgroundColor: "rgb(5,47,93)" }}
      className="min-h-screen text-white"
    >
      <Header userName="Chris George" />

      <div className="flex mt-6"> 
        {/* Sidebar for company logos and names */}
        <div className="w-1/4 bg-black bg-opacity-75 p-6 rounded-2xl ml-4"> 
          <input
            type="text"
            placeholder="Search Stocks..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full p-3 mb-6 rounded-lg  bg-white text-black focus:outline-none"
          />
          <h2 className="text-2xl font-bold mb-6">Companies</h2>
          <ul>
            {filteredCompanies.map((company) => (
              <li
                key={company.name}
                className="flex items-center gap-4 mb-4 p-4 rounded-lg hover:bg-gray-800 transition"
              >
                <img
                  src={company.logo}
                  alt={company.name}
                  className="h-10 w-10 rounded-full object-cover"
                />
                <span className="text-lg font-medium">{company.name}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default PortfolioPage;
