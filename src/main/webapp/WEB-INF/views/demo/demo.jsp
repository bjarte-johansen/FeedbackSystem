<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="no">
<head>
    <title>Client</title>
    <meta charset="UTF-8">

    <link rel="icon" href="/favicon/favicon.ico">

    <style>
        /* css for this page, client will have his own */
        html { scroll-behavior: auto; }
        body { font-family: Arial, Helvetica, sans-serif; margin: 20px; }
        input{ line-height: 1.6em; }
        table { border-collapse: collapse;  }
        table, th, td { border: 1px solid #ccc; padding: 8px; text-align:left; }
        th { background: #f0f0f0; }
        *{line-height: 1.4em;}
        .description button{ padding:8px; }

        /* unused */
        .backdrop{
            background-image: url("/html/webshop.png");
            width:100%;
            height:500px;
            margin-bottom:16px;
            display:none;
        }
    </style>

    <!-- REQUIRED PART OF SCRIPTS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <script src="/js/classes/DynamicReviewListingLoader.js" type="text/javascript"></script>

    <script>
        // usage, our code
        document.addEventListener("DOMContentLoaded", () => {
            const params = new URLSearchParams(window.location.search);
            const externalId = params.get("externalId");

            const searchParams = new URLSearchParams();
            searchParams.set("realApi", "true");
            searchParams.set("externalId", externalId !== null ? externalId : "/product/1");

            DynamicReviewListingLoader.loadReviewListing(
                document.getElementById("omtaler"),
                searchParams
            );
        });
    </script>
</head>
<body>
    <div class="description">
        <h1>Eksempel på implementasjon, dynamisk rute</h1>
        <div style="background-color:#dfdfdf; padding: 16px; border-radius: 0px; border: 1px solid #CCC; margin-bottom:16px;">
            Eksempelet vårt viser for /product/1 som angitt i javascript loader og siden viser dynamisk innlasting og
            client-side rendering.
            <br><br>
            Noen steder er det optimalisert for mobil med filtre, men ikke plasser.
            <br><br>
            <button onclick="$('.backdrop').toggle()">Toggle fake nettbutikk</button>
            <br><br>
            <h5>Stier simulerer forskjellige sider</h5>
            <button onclick="window.location.href=window.location.href.split('?')[0] + '?externalId=/product/1';">/product/1</button>
            <button onclick="window.location.href=window.location.href.split('?')[0] + '?externalId=/product/2';">/product/2</button>
            <button onclick="window.location.href=window.location.href.split('?')[0] + '?externalId=/splitter-ny-vare';">/splitter-ny-vare</button>
            <button onclick="window.location.href=window.location.href.split('?')[0] + '?externalId=/visning-disabled';">/visning-disabled</button>
            <button onclick="window.location.href=window.location.href.split('?')[0] + '?externalId=/ny-omtale-disabled';">/ny-omtale-disabled</button>
        </div>
    </div>

    <div class="fake-website">
        <div class="backdrop"></div>

        <div id="omtaler"></div>

    </div>
</body>
</html>