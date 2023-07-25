

const toggleSidebar = () =>{

    if($(".sidebar").is(":visible")){

        $(".sidebar").css("display","none");

        $(".content").css("margin-left","0%");
    }
    else{

        $(".sidebar").css("display","block");

        
        $(".content").css("margin-left","20%");
    }

};



const search=()=>{
    console.log("searching....");

    let query=$("#search-input").val();
    
    if(query == "")
    {
        $(".search-result").hide();


    }
    else{
        //search
        console.log(query);

        //sending request to server to search meach sending request to spring
        let url=`http://localhost:5050/search/${query}`;

        fetch(url).then((response)=>{
           return response.json()

        }).then((data)=>{
            //data....
            console.log(data);

            let text=`<div class='list-group'>`


            data.foreach((contact)=>{
                text+=`<a href='#' class='list-group-item list-group-action' >  ${contact.name}</a>`

            });


            text+=`</div>`


            $(".search-result").html(text);
            $(".search-result").show();



        });


        


    }
};




