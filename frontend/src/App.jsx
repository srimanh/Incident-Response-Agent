import { useState } from 'react'
import './index.css'

function App() {
  const [incidentDescription, setIncidentDescription] = useState('')
  const [environment, setEnvironment] = useState('production')
  const [reportedBy, setReportedBy] = useState('employee')
  const [response, setResponse] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setResponse(null)

    try {
      const res = await fetch('http://localhost:8080/api/incident/analyze', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          incidentDescription,
          environment,
          reportedBy,
        }),
      })

      if (!res.ok) {
        throw new Error('Failed to analyze incident')
      }

      const data = await res.json()
      setResponse(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white p-8 font-sans">
      <div className="max-w-4xl mx-auto">
        <header className="mb-12">
          <h1 className="text-4xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-500 mb-2">
            Incident Response Agent
          </h1>
          <p className="text-gray-400">AI-Powered Security Analysis & Remediation</p>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* Form Section */}
          <section className="bg-gray-800 p-6 rounded-2xl border border-gray-700 shadow-xl">
            <h2 className="text-xl font-semibold mb-6 flex items-center">
              <span className="w-2 h-6 bg-blue-500 rounded mr-3"></span>
              Report Incident
            </h2>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Incident Description
                </label>
                <textarea
                  className="w-full bg-gray-900 border border-gray-700 rounded-lg p-3 text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all h-32 resize-none"
                  placeholder="Describe the suspicious activity..."
                  value={incidentDescription}
                  onChange={(e) => setIncidentDescription(e.target.value)}
                  required
                ></textarea>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-400 mb-2">
                    Environment
                  </label>
                  <select
                    className="w-full bg-gray-900 border border-gray-700 rounded-lg p-3 text-white focus:ring-2 focus:ring-blue-500 outline-none"
                    value={environment}
                    onChange={(e) => setEnvironment(e.target.value)}
                  >
                    <option value="production">Production</option>
                    <option value="staging">Staging</option>
                    <option value="development">Development</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-400 mb-2">
                    Reported By
                  </label>
                  <select
                    className="w-full bg-gray-900 border border-gray-700 rounded-lg p-3 text-white focus:ring-2 focus:ring-blue-500 outline-none"
                    value={reportedBy}
                    onChange={(e) => setReportedBy(e.target.value)}
                  >
                    <option value="employee">Employee</option>
                    <option value="system">System Alert</option>
                    <option value="externall">External Source</option>
                  </select>
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className={`w-full py-4 rounded-xl font-bold text-lg transition-all transform active:scale-95 ${loading
                  ? 'bg-gray-700 cursor-not-allowed'
                  : 'bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 shadow-lg hover:shadow-blue-500/20'
                  }`}
              >
                {loading ? 'Analyzing...' : 'Analyze Incident'}
              </button>
            </form>
          </section>

          {/* Results Section */}
          <section className="bg-gray-800 p-6 rounded-2xl border border-gray-700 shadow-xl overflow-hidden relative">
            <h2 className="text-xl font-semibold mb-6 flex items-center">
              <span className="w-2 h-6 bg-purple-500 rounded mr-3"></span>
              Agent Analysis
            </h2>

            {!response && !loading && !error && (
              <div className="flex flex-col items-center justify-center h-64 text-gray-500">
                <div className="w-16 h-16 border-2 border-dashed border-gray-600 rounded-full mb-4 flex items-center justify-center">
                  ?
                </div>
                <p>Awaiting incident report...</p>
              </div>
            )}

            {loading && (
              <div className="flex flex-col items-center justify-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500 mb-4"></div>
                <p className="text-blue-400">Processing stages...</p>
              </div>
            )}

            {error && (
              <div className="bg-red-900/30 border border-red-500/50 p-4 rounded-xl text-red-200">
                <p className="font-bold">Error</p>
                <p className="text-sm opacity-80">{error}</p>
              </div>
            )}

            {response && (
              response.status === 'REFUSED' ? (
                <div className="flex flex-col items-center justify-center text-center p-6 space-y-4 animate-fadeIn">
                  <div className="w-16 h-16 bg-yellow-500/20 text-yellow-500 rounded-full flex items-center justify-center text-2xl border border-yellow-500/50">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                    </svg>
                  </div>
                  <h3 className="text-xl font-bold text-gray-200">Safe Refusal</h3>
                  <div className="bg-gray-900/50 border border-gray-700 p-4 rounded-xl max-w-md">
                    <p className="text-gray-400 mb-2">{response.reason}</p>
                    <p className="text-sm text-blue-400 font-medium">Suggestion: {response.suggestion}</p>
                  </div>
                </div>
              ) : (
                <div className="space-y-6 animate-fadeIn">
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="text-xs font-bold text-gray-500 uppercase tracking-widest mb-1">Classification</p>
                      <h3 className="text-2xl font-bold text-blue-400">{response.classification?.type || 'Unknown'}</h3>
                    </div>
                    <div className="text-right">
                      <p className="text-xs font-bold text-gray-500 uppercase tracking-widest mb-1">Severity</p>
                      <span className={`px-3 py-1 rounded-full text-xs font-bold ${response.severity?.level === 'HIGH' ? 'bg-red-500/20 text-red-500 border border-red-500/50' : 'bg-yellow-500/20 text-yellow-500 border border-yellow-500/50'
                        }`}>
                        {response.severity?.level || 'UNKNOWN'}
                      </span>
                    </div>
                  </div>

                  <div>
                    <p className="text-xs font-bold text-gray-500 uppercase tracking-widest mb-2">Policy References</p>
                    <div className="flex gap-2">
                      {response.policyReferences?.map(policy => (
                        <span key={policy} className="bg-gray-700 px-2 py-1 rounded text-xs text-gray-300 font-mono">
                          {policy}
                        </span>
                      ))}
                    </div>
                  </div>

                  <div className="bg-gray-900/50 p-4 rounded-xl border border-gray-700">
                    <p className="text-xs font-bold text-gray-500 uppercase tracking-widest mb-2">Recommended Actions</p>
                    <ul className="space-y-2">
                      {response.recommendedActions?.map((action, i) => (
                        <li key={action} className="text-sm text-gray-300 flex items-start">
                          <span className="text-blue-500 mr-2">•</span>
                          {action}
                        </li>
                      ))}
                    </ul>
                  </div>

                  <div className="border-t border-gray-700 pt-4">
                    <p className="text-xs font-bold text-gray-500 uppercase tracking-widest mb-1">Agent Notes</p>
                    <p className="text-sm text-gray-400 italic">"{response.notes}"</p>
                  </div>
                </div>
              )
            )}
          </section>
        </div>
      </div>
    </div>
  )
}

export default App
