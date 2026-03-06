"use client";

import { useState } from "react";

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

export default function SingleMatchPage() {
  const [file, setFile] = useState<File | null>(null);
  const [jobDescription, setJobDescription] = useState("");
  const [result, setResult] = useState<CandidateResult | null>(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      setFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
    }
  };

  const analyzeResume = async () => {
    if (!file || !jobDescription) {
      alert("Please upload a resume and provide a job description.");
      return;
    }

    setIsAnalyzing(true);
    const formData = new FormData();
    formData.append("file", file);
    formData.append("jd", jobDescription);

    try {
      const response = await fetch("http://localhost:8080/api/match", {
        method: "POST",
        body: formData,
      });

      if (!response.ok) throw new Error("Failed to analyze resume");

      const data: CandidateResult = await response.json();
      setResult(data);
    } catch (error) {
      console.error(error);
      alert("Error analyzing resume. Make sure the backend is running on port 8080.");
    } finally {
      setIsAnalyzing(false);
    }
  };

  const resetForm = () => {
    setFile(null);
    setJobDescription("");
    setResult(null);
  };

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
            { name: "Single Match", href: "/single-match" },
            { name: "Results History", href: "/results" },
            { name: "Analytics", href: "#" }
          ].map((item) => (
            <a
              key={item.name}
              href={item.href}
              className={`text-left px-4 py-3 rounded-xl transition-all duration-200 ${
                item.name === "Single Match"
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

        <div className="max-w-4xl mx-auto w-full p-8 flex flex-col gap-8">
          <header>
            <h1 className="text-3xl font-semibold tracking-tight">Single Resume Analysis</h1>
            <p className="text-white/50 mt-2">Analyze one resume against a job description with detailed scoring breakdown</p>
          </header>

          {!result ? (
            <section className="bg-white/[0.02] border border-white/10 rounded-3xl p-8 backdrop-blur-xl">
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* Resume Upload */}
                <div className="flex flex-col gap-3">
                  <label className="text-sm font-medium text-white/70">Resume (PDF)</label>
                  <div
                    onDragOver={(e) => e.preventDefault()}
                    onDrop={handleDrop}
                    className="border-2 border-dashed border-white/20 rounded-2xl flex flex-col items-center justify-center p-8 transition-all hover:bg-white/5 hover:border-white/40"
                  >
                    <input type="file" accept=".pdf" onChange={handleFileSelect} className="hidden" id="single-file-upload" />
                    <label htmlFor="single-file-upload" className="cursor-pointer flex flex-col items-center gap-4 text-center">
                      <div className="w-14 h-14 rounded-full bg-white/5 flex items-center justify-center">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="text-white/60">
                          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12" />
                        </svg>
                      </div>
                      <div>
                        <span className="text-[#A16CFF] font-medium">Click to browse</span> or drag & drop
                        <p className="text-xs text-white/40 mt-1">Accepts PDF files</p>
                      </div>
                    </label>

                    {file && (
                      <div className="mt-4 px-3 py-2 bg-white/10 rounded-full text-sm text-center">
                        {file.name}
                      </div>
                    )}
                  </div>
                </div>

                {/* Job Description */}
                <div className="flex flex-col gap-3">
                  <label className="text-sm font-medium text-white/70">Job Description</label>
                  <textarea
                    value={jobDescription}
                    onChange={(e) => setJobDescription(e.target.value)}
                    placeholder="Paste the job requirements, necessary skills, and roles here..."
                    className="h-full bg-black/40 border border-white/10 rounded-2xl p-5 text-sm resize-none focus:outline-none focus:border-[#590DF2]/60 focus:ring-1 focus:ring-[#590DF2]/60 transition-all placeholder:text-white/20"
                    rows={8}
                  />
                </div>
              </div>

              {/* Action Buttons */}
              <div className="mt-8 flex gap-4 justify-end">
                <button
                  onClick={resetForm}
                  className="px-6 py-3 rounded-xl font-medium bg-white/10 text-white/70 hover:bg-white/20 transition-all"
                >
                  Clear
                </button>
                <button
                  onClick={analyzeResume}
                  disabled={isAnalyzing || !file || !jobDescription}
                  className="bg-gradient-to-r from-[#590DF2] to-[#B026FF] hover:from-[#6B22F5] hover:to-[#C04AFF] text-white px-8 py-3.5 rounded-xl font-medium shadow-[0_0_30px_rgba(89,13,242,0.3)] transition-all active:scale-95 disabled:opacity-50 disabled:pointer-events-none flex items-center gap-2"
                >
                  {isAnalyzing ? (
                    <>
                      <svg className="animate-spin h-5 w-5 text-white" viewBox="0 0 24 24" fill="none">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      Analyzing Resume...
                    </>
                  ) : (
                    <>
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M9 11l3 3L22 4" />
                        <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11" />
                      </svg>
                      Analyze Resume
                    </>
                  )}
                </button>
              </div>
            </section>
          ) : (
            <div className="flex flex-col gap-8">
              {/* Result Header */}
              <section className="bg-white/[0.02] border border-white/10 rounded-3xl p-8 backdrop-blur-xl">
                <div className="flex items-center justify-between mb-6">
                  <div>
                    <h2 className="text-2xl font-semibold">{result.name.replace(".pdf", "")}</h2>
                    <p className="text-white/50 mt-1">Analysis Complete</p>
                  </div>
                  <button
                    onClick={resetForm}
                    className="px-4 py-2 rounded-xl text-sm font-medium bg-white/10 text-white/70 hover:bg-white/20 transition-all"
                  >
                    Analyze Another
                  </button>
                </div>

                {/* Overall Score */}
                <div className="text-center py-8">
                  <div className="inline-flex items-center justify-center w-32 h-32 rounded-full bg-gradient-to-br from-[#590DF2] to-[#B026FF] p-1">
                    <div className="w-full h-full rounded-full bg-[#0A0A0A] flex items-center justify-center">
                      <div>
                        <div className="text-4xl font-bold">{result.score.toFixed(1)}</div>
                        <div className="text-xs text-white/50">Score</div>
                      </div>
                    </div>
                  </div>
                  <div className="mt-4">
                    <span className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-medium border ${
                      result.score > 80
                        ? 'bg-[#10B981]/10 text-[#34D399] border-[#10B981]/20'
                        : result.score > 50
                          ? 'bg-[#F59E0B]/10 text-[#FBBF24] border-[#F59E0B]/20'
                          : 'bg-[#EF4444]/10 text-[#F87171] border-[#EF4444]/20'
                    }`}>
                      <div className={`w-1.5 h-1.5 rounded-full ${
                        result.score > 80 ? 'bg-[#34D399]' : result.score > 50 ? 'bg-[#FBBF24]' : 'bg-[#F87171]'
                      }`} />
                      {result.score > 80 ? 'Excellent Match' : result.score > 50 ? 'Good Match' : 'Poor Match'}
                    </span>
                  </div>
                </div>
              </section>

              {/* Detailed Breakdown */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* Score Breakdown */}
                <section className="bg-white/[0.02] border border-white/10 rounded-3xl p-6 backdrop-blur-xl">
                  <h3 className="text-lg font-medium mb-4">Score Breakdown</h3>
                  <div className="space-y-4">
                    <div>
                      <div className="flex justify-between text-sm mb-1">
                        <span className="text-white/70">Skill Score (60%)</span>
                        <span className="font-medium">{result.explanation.skillScore.toFixed(1)}</span>
                      </div>
                      <div className="w-full bg-black/60 rounded-full h-2 overflow-hidden">
                        <div
                          className="h-full bg-[#590DF2] rounded-full"
                          style={{ width: `${result.explanation.skillScore}%` }}
                        />
                      </div>
                    </div>

                    <div>
                      <div className="flex justify-between text-sm mb-1">
                        <span className="text-white/70">TF-IDF Score (40%)</span>
                        <span className="font-medium">{result.explanation.tfidfScore.toFixed(1)}</span>
                      </div>
                      <div className="w-full bg-black/60 rounded-full h-2 overflow-hidden">
                        <div
                          className="h-full bg-[#B026FF] rounded-full"
                          style={{ width: `${result.explanation.tfidfScore}%` }}
                        />
                      </div>
                    </div>

                    <div>
                      <div className="flex justify-between text-sm mb-1">
                        <span className="text-white/70">Experience Boost</span>
                        <span className="font-medium">+{result.explanation.experienceBoost.toFixed(1)}</span>
                      </div>
                      <div className="w-full bg-black/60 rounded-full h-2 overflow-hidden">
                        <div
                          className="h-full bg-[#10B981] rounded-full"
                          style={{ width: `${(result.explanation.experienceBoost / 10) * 100}%` }}
                        />
                      </div>
                    </div>

                    <div className="pt-2 border-t border-white/10">
                      <div className="flex justify-between text-sm">
                        <span className="text-white/70">Experience Years</span>
                        <span className="font-medium">{result.explanation.experienceYears} years</span>
                      </div>
                    </div>
                  </div>
                </section>

                {/* Skills Analysis */}
                <section className="bg-white/[0.02] border border-white/10 rounded-3xl p-6 backdrop-blur-xl">
                  <h3 className="text-lg font-medium mb-4">Skills Analysis</h3>
                  <div className="space-y-6">
                    <div>
                      <div className="flex items-center justify-between mb-3">
                        <h4 className="text-sm font-medium text-[#10B981]">Matched Skills ({result.matchedSkills.length})</h4>
                      </div>
                      <div className="flex flex-wrap gap-2">
                        {result.matchedSkills.length > 0 ? (
                          result.matchedSkills.map(skill => (
                            <span key={skill} className="px-3 py-1.5 rounded-lg bg-[#10B981]/20 text-[#34D399] border border-[#10B981]/30 text-sm">
                              {skill}
                            </span>
                          ))
                        ) : (
                          <span className="text-white/30 text-sm italic">No matching skills found</span>
                        )}
                      </div>
                    </div>

                    <div>
                      <div className="flex items-center justify-between mb-3">
                        <h4 className="text-sm font-medium text-[#EF4444]">Missing Skills ({result.missingSkills.length})</h4>
                      </div>
                      <div className="flex flex-wrap gap-2">
                        {result.missingSkills.length > 0 ? (
                          result.missingSkills.map(skill => (
                            <span key={skill} className="px-3 py-1.5 rounded-lg bg-[#EF4444]/20 text-[#F87171] border border-[#EF4444]/30 text-sm">
                              {skill}
                            </span>
                          ))
                        ) : (
                          <span className="text-white/30 text-sm italic">All required skills found</span>
                        )}
                      </div>
                    </div>
                  </div>
                </section>
              </div>

              {/* Category Scores */}
              {Object.keys(result.categoryScores).length > 0 && (
                <section className="bg-white/[0.02] border border-white/10 rounded-3xl p-6 backdrop-blur-xl">
                  <h3 className="text-lg font-medium mb-4">Category Performance</h3>
                  <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                    {Object.entries(result.categoryScores).map(([category, score]) => (
                      <div key={category} className="text-center p-4 bg-black/40 rounded-xl">
                        <div className="text-2xl font-bold text-[#A16CFF]">{score.toFixed(1)}</div>
                        <div className="text-xs text-white/50 mt-1">{category}</div>
                      </div>
                    ))}
                  </div>
                </section>
              )}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
