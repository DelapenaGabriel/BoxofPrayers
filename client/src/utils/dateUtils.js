/**
 * Formats a date string or timestamp into a "time ago" string.
 * @param {string|number|Date} dateInput - The date to format.
 * @returns {string} The formatted time ago string (e.g., "5m ago", "2h ago").
 */
export const formatTimeAgo = (dateInput) => {
  if (!dateInput) return ''

  let date
  try {
    // Handle different input types
    if (typeof dateInput === 'number') {
      date = new Date(dateInput)
    } else if (typeof dateInput === 'string') {
      // If it's a string of digits, treat as timestamp
      if (/^\d+$/.test(dateInput)) {
        date = new Date(parseInt(dateInput, 10))
      } else {
        // If it's an ISO string (likely), ensure we handle timezone correctly
        // If it doesn't end in Z and doesn't look like it has an offset, append Z for UTC
        // But be careful not to break other formats
        if (
          dateInput.includes('T') &&
          !dateInput.endsWith('Z') &&
          !/[+-]\d{2}:\d{2}$/.test(dateInput)
        ) {
          date = new Date(`${dateInput}Z`)
        } else {
          date = new Date(dateInput)
        }
      }
    } else if (dateInput instanceof Date) {
      date = dateInput
    } else {
      return ''
    }

    if (isNaN(date.getTime())) {
      return ''
    }

    const now = new Date()
    const diffInSeconds = Math.floor((now - date) / 1000)

    // Future dates or very loosely synchronized clocks
    if (diffInSeconds < 0) return 'Just now'

    if (diffInSeconds < 10) return 'Just now'
    if (diffInSeconds < 60) return `${diffInSeconds}s ago`
    if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`
    if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`
    if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`
    if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 604800)}w ago` // < 30 days
    if (diffInSeconds < 31536000) return `${Math.floor(diffInSeconds / 2592000)}mo ago` // < 365 days
    return `${Math.floor(diffInSeconds / 31536000)}y ago`
  } catch (e) {
    console.error('Error formatting date:', e)
    return ''
  }
}
