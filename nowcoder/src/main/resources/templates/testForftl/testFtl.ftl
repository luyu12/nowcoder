<html>

<body>
<pre>
    hello vm

    <#--你看不到我-->99888888888

    <#--
    这里都看不到
     *-->


<#list colors as color>
    ${color}
</#list>

<#list keys as key>
${key}
</#list>

    User:${user.name}
    User:${user.getName()}

<#include "header.ftl">


<#macro color colors>
    <#list colors as color>
        color by macro ${color}
    </#list>
</#macro>
<@color colors=["red","blue","green"]/>



</pre>
</body>


</html>