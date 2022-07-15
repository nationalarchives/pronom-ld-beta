async function autocomplete(input, field) {
  return $.ajax("/autocomplete", { data: { q: input } })
}
export default { autocomplete }