export default function createAddress(
  streetAddress: string,
  postalCode: string,
  postalOffice: string
) {
  return [streetAddress, postalCode, postalOffice].filter(Boolean).join(', ')
}
