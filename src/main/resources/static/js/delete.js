const deleteButton=document.querySelector(".ButtonDelate");

async function deleteBand(id){
    try {
        const request = await fetch(`/delete?id=${id}`, {
            method: 'POST'
        })
        if(!request.ok){
            throw new Error('Delete failed');
        }else{
            window.location.href="/homePage";
        }
    }catch (err){}
}
