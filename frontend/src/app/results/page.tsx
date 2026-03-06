"use client";

import { useState, useEffect } from "react";

type MatchResult = {
  id: number;
  finalScore: number;
  skillScore: number;
  tfidfScore: number;
  matchedCount: number;
  missingCount: number;
  experienceYears: number;
  candidate: {
    id: number;
    name: string;
  };
  jobDescription: {
    id: number;
    content: string;
  };
};

export default function ResultsPage() {
  const [results, setResults] = useState<MatchResult[]>([]);
  const [filteredResults, setFilteredResults] = useState<MatchResult[]>([]);
  const [loading, setLoading] = useState(true);
  const [minScore, setMinScore] = useState("");
  const [minExp, setMinExp] = useState("");
  const [showTopOnly, setShowTopOnly] = useState(false);

  const fetchResults = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/results");
      if (!response.ok) throw new Error("Failed to fetch results");
      const data: MatchResult[] = await response.json();
      setResults(data);
      setFilteredResults(data);
    } catch (error) {
      console.error(error);
      alert("Error fetching results from backend");
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = async () => {
    try {
      let url = "http://localhost:8080/api/results";
      
      if (showTopOnly) {
        url = "http://localhost:8080/api/results/top";
      } else if (minScore) {
        url = `http://localhost:8080/api/results/filterScore?minScore=${minScore}`;
      } else if (minExp) {
        url = `http://localhost:8080/api/results/filterExperience?minExp=${minExp}`;
      }

      const response = await fetch(url);
      if (!response.ok) throw new Error("Failed to fetch filtered results");
      const data: MatchResult[] = await response.json();
      setFilteredResults(data);
    } catch (error) {
      console.error(error);
      alert("Error applying filters");
    }
  };

  useEffect(() => {
    fetchResults();
  }, []);

  useEffect(() => {
    if (showTopOnly || minScore || minExp) {
      applyFilters();
    } else {
      setFilteredResults(results);
    }
  }, [minScore, minExp, showTopOnly, results]);

  const resetFilters = () => {
    setMinScore("");
    setMinExp("");
    setShowTopOnly(false);
    setFilteredResults(results);
  };

  if (loading) {
    return (
      <div className="flex h-screen bg-[#0A0A0A] text-white items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#590DF2]"></div>
      </div>
    );
  }

  return (
    <div className="flex h-screen bg-[#0A0A0A] text-white font-sans overflow-hidden">
      {/* Sidebar Navigation */}
      <aside className="w-64 border-r border-white/10 bg-black/50 backdrop-blur-xl p-6 flex flex-col gap-8 hidden md:flex">
        <div className="flex items-center gap-3 text-xl font-bold tracking-tight">
          <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-[#590DF2] to-[#B026FF] flex items-center justify-center shadow-[0_0_20px_rgba(89,13,242,0.4)]">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              <path d="M2 17L12 22L22 17" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              <path d="M2 12L12 17L22 12" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </div>
          Quantum ATS
        </div>

        <nav className="flex flex-col gap-2">
          {[
            { name: "Dashboard", href: "/" },
            { name: "Results History", href: "/results" },
            { name: "Analytics", href: "#" },
            { name: "Settings", href: "#" }
          ].map((item) => (
            <a
              key={item.name}
              href={item.href}
              className={`text-left px-4 py-3 rounded-xl transition-all duration-200 ${
                item.name === "Results History"
                  ? "bg-white/5 text-white shadow-inner font-medium"
                  : "text-white/50 hover:text-white hover:bg-white/5"
              }`}
            >
              {item.name}
            </a>
          ))}
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col h-full overflow-y-auto relative">
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[800px] h-[300px] bg-[#590DF2]/20 blur-[120px] rounded-full pointer-events-none -z-10" />

        <div className="max-w-6xl mx-auto w-full p-8 flex flex-col gap-8">
          <header>
            <h1 className="text-3xl font-semibold tracking-tight">Results History</h1>
            <p className="text-white/50 mt-2">View and filter all candidate evaluations</p>
          </header>

          {/* Filters Section */}
          <section className="bg-white/[0.02] border border-white/10 rounded-3xl p-6 backdrop-blur-xl">
            <h3 className="text-lg font-medium mb-4">Filters</h3>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-white/70">Minimum Score</label>
                <input
                  type="number"
                  placeholder="e.g. 80"
                  value={minScore}
                  onChange={(e) => setMinScore(e.target.value)}
                  min="0"
                  max="100"
                  className="bg-black/40 border border-white/10 rounded-xl px-4 py-2 text-sm focus:outline-none focus:border-[#590DF2]/60 transition-all placeholder:text-white/20"
                />
              </div>
              
              <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-white/70">Minimum Experience (Years)</label>
                <input
                  type="number"
                  placeholder="e.g. 3"
                  value={minExp}
                  onChange={(e) => setMinExp(e.target.value)}
                  min="0"
                  className="bg-black/40 border border-white/10 rounded-xl px-4 py-2 text-sm focus:outline-none focus:border-[#590DF2]/60 transition-all placeholder:text-white/20"
                />
              </div>

              <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-white/70">Top Candidates Only</label>
                <button
                  onClick={() => setShowTopOnly(!showTopOnly)}
                  className={`px-4 py-2 rounded-xl text-sm font-medium transition-all ${
                    showTopOnly
                      ? "bg-[#590DF2] text-white"
                      : "bg-black/40 border border-white/10 text-white/70 hover:bg-white/5"
                  }`}
                >
                  {showTopOnly ? "Enabled" : "Enable"}
                </button>
              </div>

              <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-white/70">&nbsp;</label>
                <button
                  onClick={resetFilters}
                  className="px-4 py-2 rounded-xl text-sm font-medium bg-white/10 text-white/70 hover:bg-white/20 transition-all"
                >
                  Reset Filters
                </button>
              </div>
            </div>
          </section>

          {/* Results Table */}
          <section className="bg-white/[0.02] border border-white/10 rounded-3xl overflow-hidden backdrop-blur-xl">
            <div className="p-6 border-b border-white/10">
              <h3 className="text-lg font-medium">
                {filteredResults.length} {filteredResults.length === 1 ? "Result" : "Results"}
              </h3>
            </div>

            {filteredResults.length === 0 ? (
              <div className="p-12 text-center text-white/50">
                <div className="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center mx-auto mb-4">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </div>
                <p>No results found matching your criteria</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="border-b border-white/10 text-xs uppercase tracking-wider text-white/40">
                      <th className="p-5 font-medium">Candidate</th>
                      <th className="p-5 font-medium">Final Score</th>
                      <th className="p-5 font-medium">Skill Score</th>
                      <th className="p-5 font-medium">TF-IDF Score</th>
                      <th className="p-5 font-medium">Experience</th>
                      <th className="p-5 font-medium">Matched/Missing</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-white/5">
                    {filteredResults.map((result) => (
                      <tr key={result.id} className="hover:bg-white/5 transition-colors">
                        <td className="p-5">
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-white/10 to-white/5 flex items-center justify-center font-medium border border-white/10">
                              {result.candidate.name.substring(0, 1).toUpperCase()}
                            </div>
                            <div>
                              <div className="font-medium text-white/90">
                                {result.candidate.name}
                              </div>
                              <div className="text-xs text-white/40">ID: {result.id}</div>
                            </div>
                          </div>
                        </td>

                        <td className="p-5">
                          <div className="flex flex-col gap-1">
                            <div className="flex items-end gap-1.5">
                              <span className="text-2xl font-semibold tracking-tighter tabular-nums leading-none">
                                {result.finalScore.toFixed(1)}
                              </span>
                              <span className="text-xs text-white/40 mb-0.5">/ 100</span>
                            </div>
                            <div className="w-full bg-black/60 rounded-full h-1.5 overflow-hidden">
                              <div
                                className={`h-full rounded-full ${
                                  result.finalScore > 80 ? 'bg-[#10B981]' : result.finalScore > 50 ? 'bg-[#F59E0B]' : 'bg-[#EF4444]'
                                }`}
                                style={{ width: `${Math.min(result.finalScore, 100)}%` }}
                              />
                            </div>
                          </div>
                        </td>

                        <td className="p-5 text-sm text-white/60 tabular-nums">
                          {result.skillScore.toFixed(1)}
                        </td>

                        <td className="p-5 text-sm text-white/60 tabular-nums">
                          {result.tfidfScore.toFixed(1)}
                        </td>

                        <td className="p-5 text-sm text-white/60 tabular-nums">
                          {result.experienceYears} Years
                        </td>

                        <td className="p-5">
                          <div className="flex gap-4 text-sm">
                            <span className="text-[#10B981]">
                              {result.matchedCount} matched
                            </span>
                            <span className="text-[#EF4444]">
                              {result.missingCount} missing
                            </span>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </div>
      </main>
    </div>
  );
}
