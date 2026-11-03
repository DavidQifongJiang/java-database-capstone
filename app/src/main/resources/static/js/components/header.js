// Header state is presentation only; every protected API still validates its JWT.
function updateHeader(){
  const token=localStorage.getItem('token');
  const userRole=localStorage.getItem('userRole');
  document.querySelector('#logout').hidden=!(token&&userRole);
  document.querySelector('#accountName').textContent=token&&userRole?(localStorage.getItem('clinicName')||userRole):'';
}
