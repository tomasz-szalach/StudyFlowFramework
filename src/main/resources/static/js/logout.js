 async function logout() {
     try {
         const resp = await fetch('/logout', {method: 'POST'});
         if (!resp.ok) {
             throw new Error('Logout failed');
         }else{
             window.location.href="/login";
         }
     } catch (err) {
     }
 }

 