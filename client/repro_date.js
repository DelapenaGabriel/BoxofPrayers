const now = new Date()

function formatTimeAgo(dateStr) {
  if (!dateStr) return ''

  try {
    // Ensure we treat the date as UTC if it doesn't specify timezone
    // This is the logic from the component
    const date = new Date(dateStr.endsWith('Z') ? dateStr : `${dateStr}Z`)
    const diffInSeconds = Math.floor((now - date) / 1000)

    console.log(`Input: ${typeof dateStr} "${dateStr}" -> Date: ${date} -> Diff: ${diffInSeconds}`)

    if (diffInSeconds < 10) return 'Just now'
    if (diffInSeconds < 60) return `${diffInSeconds}s ago`
    if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`
    if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`
    if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`
    if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 604800)}w ago`
    if (diffInSeconds < 31536000) return `${Math.floor(diffInSeconds / 2592000)}mo ago`
    return `${Math.floor(diffInSeconds / 31536000)}y ago`
  } catch (e) {
    console.log(`Input: ${dateStr} -> Error: ${e.message}`)
    return 'ERROR'
  }
}

// Test cases
const cases = [
  '2023-10-10T10:00:00', // Standard ISO without Z
  '2023-10-10T10:00:00Z', // ISO with Z
  '2023-10-10 10:00:00', // SQL style
  '2023-10-10', // Just date
  1696932000000, // Timestamp number
  '1696932000000', // Timestamp string
  null,
  undefined,
  '2024-02-10T12:00:00.123456', // High precision
  [], // Array
  {}, // Object
]

cases.forEach((c) => {
  try {
    const res = formatTimeAgo(c)
    console.log(`Result: "${res}"\n`)
  } catch (e) {
    console.log(`Top level error: ${e}\n`)
  }
})
