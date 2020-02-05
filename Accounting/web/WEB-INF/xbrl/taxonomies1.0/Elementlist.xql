xquery version "1.0";

declare namespace xdt = "http://xbrl.org/2005/xdt";
declare namespace link = "http://www.xbrl.org/2003/linkbase";
declare namespace xbrli = "http://www.xbrl.org/2003/instance";
declare namespace xlink = "http://www.w3.org/1999/xlink";
declare namespace ref = "http://www.xbrl.org/2004/ref";
declare namespace dt_de = "http://xbrl.bundesbank.de/dt-de";
declare namespace xs = "http://www.w3.org/2001/XMLSchema";
declare namespace xsd = "http://www.w3.org/2001/XMLSchema";

declare namespace fbbk = "http://www.bundesbank.de/xquery/functions/2006-01-01";

(: Ermittle die importierte Taxonomy aus den import-Elementen entsprechend dem uebergebenen Praefix :)
declare function fbbk:getImportedTaxonomyName ( $parentTaxonomy as node()) as xs:string*
{
	let $seq := (for $importedTaxonomy in $parentTaxonomy//xs:import//@schemaLocation
						where fn:starts-with(fn:string($importedTaxonomy ), "t-")
								return $importedTaxonomy)
	return (:$seq:)
	
	for $templateTaxonomyName in $seq
	return
		let$seq2 := (for $importTaxonomy in doc($templateTaxonomyName)//xs:import//@schemaLocation
							where fn:starts-with(fn:string($importTaxonomy ), "p-")
							or fn:starts-with(fn:string($importTaxonomy ), "d-")
							return $importTaxonomy) 
	return $templateTaxonomyName union $seq2
};

(: Trenne Elementname und ID nach 50 Zeichen :)
declare function fbbk:cutString($string as xs:string) as xs:string
{		
	let $x := $string
	return (
	if (fn:string-length($x) > 75)
	then fn:concat(fn:substring($x, 0, 75), "&#xD;", fn:substring($x, 75, fn:string-length($x)  - 74))
	else $x )
};


(:Ermittle das jeweilige spezifizierte Label fuer einen Posten, z. B. Betragsbasis, Position, Bezeichnung:)
declare function fbbk:getLabel($taxonomy as node(), $elementId as xs:string, $type as xs:string) as xs:string*
{
	for $locator in fbbk:getLinkbase($taxonomy,"label")//link:loc					
		 where fn:substring-after($locator//@xlink:href,"#") = $elementId		
		return
		for $labelArc in fbbk:getLinkbase($taxonomy,"label")//link:labelArc					
		 where $locator/@xlink:label = $labelArc/@xlink:from 
		return 
		for $label in fbbk:getLinkbase($taxonomy,"label")//link:label					
		 where $labelArc/@xlink:to = $label/@xlink:label
			  and $label//@xlink:role = $type
		return
			   $label/text()
};

(:Ermittle die entsprechende Linkbase :)
declare function fbbk:getLinkbase ( $taxonomy as node(), $type as xs:string ) as node()
{
	for $linkbase in $taxonomy//link:linkbaseRef//@xlink:href 
		where fn:contains(fn:string($linkbase), $type)
		return doc($linkbase) 
};

let $file := xs:string("c-eu-2005-12-31.xsd")

return
document{
	element html {
	element link { attribute href { "../../css/xbrl.css" }, attribute rel { "stylesheet" }, attribute type { "text/css" } },
	element body {
	element h2 {  element span { attribute class { "header" },"COREP TAXONOMY - ELEMENT LIST"}},
	for $importTaxonomy in fn:distinct-values(fbbk:getImportedTaxonomyName(doc($file ))) 
		order by $importTaxonomy
	return
	element p{
	element table { attribute border { "1" }, attribute width { "100%" }, attribute cellpadding { "0" }, attribute cellspacing { "0" }, 
						  attribute rules { "cols" },
	element tr {
	element td { attribute colspan { "3" },   element span { attribute class { "header" },  
					  xs:string(doc($importTaxonomy)//@targetNamespace)},
	element br {}, element br {} }
					},
	element tr {
				 element td { attribute width { "33%" }, 
				 element span { attribute class { "contenido" }, element strong {"NAME"}}},
				 element td { attribute width { "33%" }, 
				 element span { attribute class { "contenido" }, element strong {"ID"}}},
				element td { attribute width { "33%" }, 
				 element span { attribute class { "contenido" }, element strong {"LABEL"}}}
				 } ,											
		for $elements in doc($importTaxonomy)//xsd:element
			order by $elements/@name
			 return 			 
				 if (fn:exists($elements/@id) )
					 then element tr {
					 element td { element span { attribute class { "contenido" },  xs:string($elements/@name)}},
					 element td { attribute style { "white-space:nowrap" }, element span { attribute class { "contenido" }, xs:string($elements/@id)}},
					 element td { attribute style { "white-space:nowrap" }, element span { attribute class { "contenido" }, 
										fbbk:getLabel(doc($importTaxonomy), xs:string($elements/@id), "http://www.xbrl.org/2003/role/label")}}
					 }
					 else xs:string("")
		}
		}
		}
		}
}
