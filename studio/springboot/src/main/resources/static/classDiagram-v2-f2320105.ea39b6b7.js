function e(e,t,l,a){Object.defineProperty(e,t,{get:l,set:a,enumerable:!0,configurable:!0})}var t=globalThis.parcelRequire94c2,l=t.register;l("6yDN9",function(l,a){e(l.exports,"diagram",()=>h);var i=t("c0bU2"),n=t("2YFJl"),o=t("4LkSm"),r=t("b34XJ"),s=t("15VEc");t("eJNXH"),t("gngdn"),t("2ujND"),t("i8Fxz"),t("hV1gR"),t("c0ySZ");let d=e=>(0,r.e).sanitizeText(e,(0,r.c)()),c={dividerMargin:10,padding:5,textHeight:10,curve:void 0},b=function(e,t,l,a){let i=Object.keys(e);(0,r.l).info("keys:",i),(0,r.l).info(e),i.forEach(function(i){var n,o;let s=e[i],c={shape:"rect",id:s.id,domId:s.domId,labelText:d(s.id),labelStyle:"",style:"fill: none; stroke: black",padding:(null==(n=(0,r.c)().flowchart)?void 0:n.padding)??(null==(o=(0,r.c)().class)?void 0:o.padding)};t.setNode(s.id,c),p(s.classes,t,l,a,s.id),(0,r.l).info("setNode",c)})},p=function(e,t,l,a,i){let n=Object.keys(e);(0,r.l).info("keys:",n),(0,r.l).info(e),n.filter(t=>e[t].parent==i).forEach(function(l){var n,o;let s=e[l],c=s.cssClasses.join(" "),b=(0,r.k)(s.styles),p=s.label??s.id,u={labelStyle:b.labelStyle,shape:"class_box",labelText:d(p),classData:s,rx:0,ry:0,class:c,style:b.style,id:s.id,domId:s.domId,tooltip:a.db.getTooltip(s.id,i)||"",haveCallback:s.haveCallback,link:s.link,width:"group"===s.type?500:void 0,type:s.type,padding:(null==(n=(0,r.c)().flowchart)?void 0:n.padding)??(null==(o=(0,r.c)().class)?void 0:o.padding)};t.setNode(s.id,u),i&&t.setParent(s.id,i),(0,r.l).info("setNode",u)})},u=function(e,t,l,a){(0,r.l).info(e),e.forEach(function(e,i){var o,s;let b={labelStyle:"",shape:"note",labelText:d(e.text),noteData:e,rx:0,ry:0,class:"",style:"",id:e.id,domId:e.id,tooltip:"",type:"note",padding:(null==(o=(0,r.c)().flowchart)?void 0:o.padding)??(null==(s=(0,r.c)().class)?void 0:s.padding)};if(t.setNode(e.id,b),(0,r.l).info("setNode",b),!e.class||!(e.class in a))return;let p=l+i,u={id:`edgeNote${p}`,classes:"relation",pattern:"dotted",arrowhead:"none",startLabelRight:"",endLabelLeft:"",arrowTypeStart:"none",arrowTypeEnd:"none",style:"fill:none",labelStyle:"",curve:(0,r.n)(c.curve,n.curveLinear)};t.setEdge(e.id,e.class,u,p)})},f=function(e,t){let l=(0,r.c)().flowchart,a=0;e.forEach(function(e){var i;a++;let o={classes:"relation",pattern:1==e.relation.lineType?"dashed":"solid",id:`id_${e.id1}_${e.id2}_${a}`,arrowhead:"arrow_open"===e.type?"none":"normal",startLabelRight:"none"===e.relationTitle1?"":e.relationTitle1,endLabelLeft:"none"===e.relationTitle2?"":e.relationTitle2,arrowTypeStart:y(e.relation.type1),arrowTypeEnd:y(e.relation.type2),style:"fill:none",labelStyle:"",curve:(0,r.n)(null==l?void 0:l.curve,n.curveLinear)};if((0,r.l).info(o,e),void 0!==e.style){let t=(0,r.k)(e.style);o.style=t.style,o.labelStyle=t.labelStyle}e.text=e.title,void 0===e.text?void 0!==e.style&&(o.arrowheadStyle="fill: #333"):(o.arrowheadStyle="fill: #333",o.labelpos="c",(null==(i=(0,r.c)().flowchart)?void 0:i.htmlLabels)??(0,r.c)().htmlLabels?(o.labelType="html",o.label='<span class="edgeLabel">'+e.text+"</span>"):(o.labelType="text",o.label=e.text.replace(r.e.lineBreakRegex,"\n"),void 0===e.style&&(o.style=o.style||"stroke: #333; stroke-width: 1.5px;fill:none"),o.labelStyle=o.labelStyle.replace("color:","fill:"))),t.setEdge(e.id1,e.id2,o,a)})},g=async function(e,t,l,a){let i;(0,r.l).info("Drawing class - ",t);let d=(0,r.c)().flowchart??(0,r.c)().class,c=(0,r.c)().securityLevel;(0,r.l).info("config:",d);let g=(null==d?void 0:d.nodeSpacing)??50,y=(null==d?void 0:d.rankSpacing)??50,h=new o.Graph({multigraph:!0,compound:!0}).setGraph({rankdir:a.db.getDirection(),nodesep:g,ranksep:y,marginx:8,marginy:8}).setDefaultEdgeLabel(function(){return{}}),v=a.db.getNamespaces(),w=a.db.getClasses(),x=a.db.getRelations(),m=a.db.getNotes();(0,r.l).info(x),b(v,h,t,a),p(w,h,t,a),f(x,h),u(m,h,x.length+1,w),"sandbox"===c&&(i=(0,n.select)("#i"+t));let k="sandbox"===c?(0,n.select)(i.nodes()[0].contentDocument.body):(0,n.select)("body"),T=k.select(`[id="${t}"]`),S=k.select("#"+t+" g");if(await (0,s.r)(S,h,["aggregation","extension","composition","dependency","lollipop"],"classDiagram",t),(0,r.u).insertTitle(T,"classTitleText",(null==d?void 0:d.titleTopMargin)??5,a.db.getDiagramTitle()),(0,r.o)(h,T,null==d?void 0:d.diagramPadding,null==d?void 0:d.useMaxWidth),!(null==d?void 0:d.htmlLabels)){let e="sandbox"===c?i.nodes()[0].contentDocument:document;for(let l of e.querySelectorAll('[id="'+t+'"] .edgeLabel .label')){let t=l.getBBox(),a=e.createElementNS("http://www.w3.org/2000/svg","rect");a.setAttribute("rx",0),a.setAttribute("ry",0),a.setAttribute("width",t.width),a.setAttribute("height",t.height),l.insertBefore(a,l.firstChild)}}};function y(e){let t;switch(e){case 0:t="aggregation";break;case 1:t="extension";break;case 2:t="composition";break;case 3:t="dependency";break;case 4:t="lollipop";break;default:t="none"}return t}let h={parser:i.p,db:i.d,renderer:{setConf:function(e){c={...c,...e}},draw:g},styles:i.s,init:e=>{e.class||(e.class={}),e.class.arrowMarkerAbsolute=e.arrowMarkerAbsolute,(0,i.d).clear()}}}),l("hNIl0",function(l,a){e(l.exports,"default",()=>n);var i=t("gbpSA"),n=function(e){return(0,i.default)(e,4)}});
//# sourceMappingURL=classDiagram-v2-f2320105.ea39b6b7.js.map
