"use client";

import { useState } from "react";

// Types matching our Spring Boot API
type ScoreExplanation = {
  skillScore: number;
  tfidfScore: number;
  experienceYears: number;
  experienceBoost: number;
  categoryScores: Record<string, number>;
};

type CandidateResult = {
  name: string;
  score: number;
  matchedSkills: string[];
  missingSkills: string[];
  categoryScores: Record<string, number>;
  explanation: ScoreExplanation;
};

export default function Dashboard() {
  const [files, setFiles] = useState<File[]>([]);
  const [jobDescription, setJobDescription] = useState("");
  const [results, setResults] = useState<CandidateResult[]>([]);
  const [isAnalyzing, setIsAnalyzing] = useState(false);

  // Handle Drag & Drop
  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    if (e.dataTransfer.files) {
      setFiles(Array.from(e.dataTransfer.files));
    }
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const newFiles = Array.from(e.target.files);
      setFiles(prevFiles => {
        // Filter out duplicates based on file name
        const existingNames = prevFiles.map(f => f.name);
        const uniqueNewFiles = newFiles.filter(f => !existingNames.includes(f.name));
        return [...prevFiles, ...uniqueNewFiles];
      });
    }
  };

  const removeFile = (fileName: string) => {
    setFiles(prevFiles => prevFiles.filter(f => f.name !== fileName));
  };

  const clearAllFiles = () => {
    setFiles([]);
  };

  const analyzeCandidates = async () => {
    if (files.length === 0 || !jobDescription) {
      alert("Please upload at least one resume and provide a job description.");
      return;
    }

    setIsAnalyzing(true);
    const formData = new FormData();
    formData.append("jd", jobDescription);
    files.forEach((file) => formData.append("files", file));

    try {
      // Calling our Spring Boot Backend
      const response = await fetch("http://localhost:8080/api/rank", {
        method: "POST",
        body: formData,
      });

      if (!response.ok) throw new Error("Failed to analyze candidates");

      const data: CandidateResult[] = await response.json();
      setResults(data);
    } catch (error) {
      console.error(error);
      alert("Error contacting the ATS Backend. Make sure Spring Boot is running on port 8080.");
    } finally {
      setIsAnalyzing(false);
    }
  };

  return (
    <div className="flex h-screen bg-[#0A0A0A] text-white font-sans overflow-hidden selection:bg-[#590DF2]/30">

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
            { name: "Single Match", href: "/single-match" },
            { name: "Results History", href: "/results" },
            { name: "Analytics", href: "#" }
          ].map((item) => (
            <a
              key={item.name}
              href={item.href}
              className={`text-left px-4 py-3 rounded-xl transition-all duration-200 ${
                item.name === "Dashboard"
                  ? "bg-white/5 text-white shadow-inner font-medium"
                  : "text-white/50 hover:text-white hover:bg-white/5"
              }`}
            >
              {item.name}
            </a>
          ))}
        </nav>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 flex flex-col h-full overflow-y-auto relative">

        {/* Decorative Top Glow */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[800px] h-[300px] bg-[#590DF2]/20 blur-[120px] rounded-full pointer-events-none -z-10" />

        <div className="max-w-6xl mx-auto w-full p-8 flex flex-col gap-10">

          <header>
            <h1 className="text-3xl font-semibold tracking-tight">AI Candidate Evaluation</h1>
            <p className="text-white/50 mt-2">Rank resumes instantly using our ONNX hybrid machine learning model.</p>
          </header>

          {/* New Evaluation Form (Glassmorphism Card) */}
          <section className="bg-white/[0.02] border border-white/10 rounded-3xl p-8 backdrop-blur-xl shadow-2xl relative overflow-hidden group">

            <div className="absolute inset-0 bg-gradient-to-b from-white/[0.04] to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none" />

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 relative z-10">

              {/* Drag & Drop Resumes */}
              <div className="flex flex-col gap-3">
                <label className="text-sm font-medium text-white/70">Candidate Resumes (PDF)</label>
                <div
                  onDragOver={(e) => e.preventDefault()}
                  onDrop={handleDrop}
                  className="flex-1 border-2 border-dashed border-white/20 rounded-2xl flex flex-col items-center justify-center p-8 transition-all hover:bg-white/5 hover:border-white/40 group/drop"
                >
                  <input type="file" multiple accept=".pdf" onChange={handleFileSelect} className="hidden" id="file-upload" />
                  <label htmlFor="file-upload" className="cursor-pointer flex flex-col items-center gap-4 text-center">
                    <div className="w-14 h-14 rounded-full bg-white/5 flex items-center justify-center group-hover/drop:scale-110 transition-transform">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="text-white/60">
                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12" />
                      </svg>
                    </div>
                    <div>
                      <span className="text-[#A16CFF] font-medium">Click to browse</span> or drag & drop
                      <p className="text-xs text-white/40 mt-1">Accepts multiple PDF files</p>
                    </div>
                  </label>

                  {files.length > 0 && (
                    <div className="mt-4 w-full">
                      <div className="flex justify-between items-center mb-2">
                        <span className="text-xs text-white/50">{files.length} file(s) selected</span>
                        <button
                          onClick={clearAllFiles}
                          className="text-xs text-red-400 hover:text-red-300 transition-colors"
                        >
                          Clear all
                        </button>
                      </div>
                      <div className="flex flex-wrap gap-2 max-h-32 overflow-y-auto">
                        {files.map(f => (
                          <div key={f.name} className="px-3 py-1 bg-white/10 rounded-full text-xs truncate max-w-[150px] flex items-center gap-2 group">
                            <span className="truncate">{f.name}</span>
                            <button
                              onClick={() => removeFile(f.name)}
                              className="text-white/60 hover:text-red-400 transition-colors opacity-0 group-hover:opacity-100"
                              title="Remove file"
                            >
                              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <path d="M18 6L6 18M6 6l12 12" />
                              </svg>
                            </button>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>

              {/* Job Description */}
              <div className="flex flex-col gap-3">
                <label className="text-sm font-medium text-white/70">Target Job Description</label>
                <textarea
                  value={jobDescription}
                  onChange={(e) => setJobDescription(e.target.value)}
                  placeholder="Paste the job requirements, necessary skills, and roles here..."
                  className="flex-1 w-full bg-black/40 border border-white/10 rounded-2xl p-5 text-sm resize-none focus:outline-none focus:border-[#590DF2]/60 focus:ring-1 focus:ring-[#590DF2]/60 transition-all placeholder:text-white/20"
                />
              </div>

            </div>

            {/* Action Button */}
            <div className="mt-8 flex justify-end">
              <button
                onClick={analyzeCandidates}
                disabled={isAnalyzing}
                className="bg-gradient-to-r from-[#590DF2] to-[#B026FF] hover:from-[#6B22F5] hover:to-[#C04AFF] text-white px-8 py-3.5 rounded-xl font-medium shadow-[0_0_30px_rgba(89,13,242,0.3)] transition-all active:scale-95 disabled:opacity-50 disabled:pointer-events-none flex items-center gap-2"
              >
                {isAnalyzing ? (
                  <>
                    <svg className="animate-spin h-5 w-5 text-white" viewBox="0 0 24 24" fill="none">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Running ML Pipeline...
                  </>
                ) : (
                  <>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M5 12h14M12 5l7 7-7 7" />
                    </svg>
                    Analyze Candidates with AI
                  </>
                )}
              </button>
            </div>

          </section>

          {/* Results Section */}
          {results.length > 0 && (
            <section className="flex flex-col gap-6 animate-in slide-in-from-bottom-8 duration-700 ease-out fade-in">
              <h2 className="text-xl font-semibold flex items-center gap-3">
                Recent Results
                <span className="px-2.5 py-0.5 rounded-full bg-white/10 text-xs text-white/60 font-medium">
                  {results.length} Candidates
                </span>
              </h2>

              <div className="bg-white/[0.02] border border-white/10 rounded-3xl overflow-hidden backdrop-blur-xl">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="border-b border-white/10 text-xs uppercase tracking-wider text-white/40">
                      <th className="p-5 font-medium">Candidate</th>
                      <th className="p-5 font-medium">Hybrid Score</th>
                      <th className="p-5 font-medium">Experience</th>
                      <th className="p-5 font-medium w-1/3">Matched Skills</th>
                      <th className="p-5 font-medium text-right">Status</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-white/5">
                    {results.map((c, i) => (
                      <tr key={i} className="hover:bg-white/5 transition-colors group">

                        <td className="p-5">
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-white/10 to-white/5 flex items-center justify-center font-medium border border-white/10">
                              {c.name.substring(0, 1).toUpperCase()}
                            </div>
                            <div>
                              <div className="font-medium text-white/90 truncate max-w-[200px]" title={c.name}>
                                {c.name.replace(".pdf", "")}
                              </div>
                              <div className="text-xs text-white/40 mt-0.5">PDF Document</div>
                            </div>
                          </div>
                        </td>

                        <td className="p-5">
                          <div className="flex flex-col gap-1">
                            <div className="flex items-end gap-1.5">
                              <span className="text-2xl font-semibold tracking-tighter tabular-nums leading-none">
                                {c.score.toFixed(1)}
                              </span>
                              <span className="text-xs text-white/40 mb-0.5">/ 100</span>
                            </div>
                            <div className="w-full bg-black/60 rounded-full h-1.5 overflow-hidden">
                              <div
                                className={`h-full rounded-full ${c.score > 80 ? 'bg-[#10B981]' : c.score > 50 ? 'bg-[#F59E0B]' : 'bg-[#EF4444]'}`}
                                style={{ width: `${Math.min(c.score, 100)}%` }}
                              />
                            </div>
                          </div>
                        </td>

                        <td className="p-5 text-sm text-white/60 tabular-nums">
                          {c.explanation.experienceYears} Years
                        </td>

                        <td className="p-5">
                          <div className="flex flex-wrap gap-1.5">
                            {c.matchedSkills.slice(0, 5).map(skill => (
                              <span key={skill} className="px-2 py-1 rounded-md bg-[#590DF2]/20 text-[#D2BAFF] border border-[#590DF2]/30 text-xs">
                                {skill}
                              </span>
                            ))}
                            {c.matchedSkills.length > 5 && (
                              <span className="px-2 py-1 rounded-md bg-white/5 text-white/40 text-xs">
                                +{c.matchedSkills.length - 5}
                              </span>
                            )}
                            {c.matchedSkills.length === 0 && (
                              <span className="text-white/30 text-xs italic">No matching skills</span>
                            )}
                          </div>
                        </td>

                        <td className="p-5 text-right">
                          <span className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-medium border
                            ${c.score > 80
                              ? 'bg-[#10B981]/10 text-[#34D399] border-[#10B981]/20'
                              : c.score > 50
                                ? 'bg-[#F59E0B]/10 text-[#FBBF24] border-[#F59E0B]/20'
                                : 'bg-[#EF4444]/10 text-[#F87171] border-[#EF4444]/20'
                            }`
                          }>
                            <div className={`w-1.5 h-1.5 rounded-full 
                               ${c.score > 80 ? 'bg-[#34D399]' : c.score > 50 ? 'bg-[#FBBF24]' : 'bg-[#F87171]'}`
                            } />
                            {c.score > 80 ? 'Top Match' : c.score > 50 ? 'Average' : 'Poor Match'}
                          </span>
                        </td>

                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
          )}

        </div>
      </main>
    </div>
  );
}
