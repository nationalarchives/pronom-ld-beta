const reverseMapping = {
  "form": {
    "Core": "1",
    "Main": "1.1",
    "File format name": "1.1.1",
    "File format description": "1.1.3",
    "Format type": "1.1.4",
    "References": "1.2",
    "Name": "4.1",
    "Link URL": "1.2.2",
    "Documentation": "1.2.3",
    "Author": "1.2.4",
    "Identifiers": "3.2",
    "Publication date": "1.2.6",
    "Type": "1.2.7",
    "Note": "1.2.8",
    "Signatures": "2",
    "PUID and name": "2.1",
    "Choose signature type": "2.1.1",
    "Signature name": "2.3.1",
    "Generic flag": "2.1.3",
    "Byte sequences": "3.4.1",
    "Select endianess": "2.4.1",
    "Description": "2.4.2",
    "Binary value": "2.4.3",
    "Position type": "2.4.4",
    "Min offset": "2.4.5",
    "Max offset": "2.4.6",
    "Container signatures": "2.3",
    "Path": "2.4.7",
    "More information": "3",
    "Priority": "3.1",
    "File format": "3.1.1",
    "Identifiers Type": "3.2.1",
    "Identifier": "3.2.2",
    "Aliases": "3.2.3",
    "Relationships": "3.3",
    "Choose relationship type": "3.3.1",
    "Related file format": "3.3.2",
    "Format families": "3.3.3",
    "Additional properties": "3.4",
    "Select file format byte order": "3.4.1.1",
    "Compression Type": "3.4.1.2",
    "Stakeholder and specification": "3.4.2",
    "Developer": "3.4.2.1",
    "Your details": "4",
    "Organisation": "4.2",
    "Contact email": "4.3",
    "Country": "4.4",
    "Comment": "4.5",
    "Recognition": "4.6.1",
    "Recognition - stay annonymous": "4.6.2",
    "Home page": "5",
    "Download test notes ": "5.1",
    "Developer": "5.2.1",
    "Support": "5.2.2",
    "Format IPR": "5.2.3",
    "Document ": "5.2.4",
    "Release Date": "5.2.5",
    "Withdrawn": "5.2.6",
    "Withdrawn Date": "5.2.7",
    "Technical Enviroment": "5.3.1",
    "Actors": "6",
    "Date ": "6.1",
    "Actor ": "6.2",
    "Action": "6.3",
    "Priority": "6.4",
  }
}


module.exports = {
  form: {
    "1.1.1": "This is the name of the file format in the specification, or if not known then the name that best describes the format. Each word should be capatilised, unless the specification says otherwise e.g. 'exFAT'. If the format has multiple names these can be placed in the alias field.",
    "1.1.2": "This field indicates the version number of the file format. There might not be a version. If it is a version number then it is written as a numeral e.g. '1' or '1.1'. Sometimes the version number is a range, e.g. 1-5. Sometimes versions are not numbers in which case use the standard text e.g. 'Alpha' or if multiple versions of the same file format are in the database it can be written in as 'Generic'.",
    "1.1.3": "Write a few sentences describing the file format. Descriptions must be objective, no commercial statements e.g. 'this software is best for'. Online sources can be used, preferably multiple sources, but rewritten into your own words. Areas that could be covered in the description include a timeline of development and support, the function of the file format and software and details of the format specification. The description should also include information relevant to it's identification, preservation and the conditions you might encounter it. For example what different extensions may refer to, whether the size of the file has any relevance or if other files are normally found in conjunction with this format.",
    "1.1.4": "The format type that most closely resembles the format. Leave blank if none are suitable.",
    "1.2.1": "The name of the documentation, standard or webpage.",
    "1.2.2": "Enter the link to the webpage. Check the link works once in the table.",
    "1.2.3": "Select if reference is a documentation",
    "1.2.4": "Author of the article as it appears on the webpage or the documentation.",
    "1.2.5": "Other identifying features of the file format. For example MIME type or apple resource fork. PRONOM only accepts MIME types from official documentation or from the IANA MIME type list.",
    "1.2.6": "The date of publication on the website or documentation.",
    "1.2.7": "Format of the publication. This could be document or website.",
    "1.2.8": "Any other relevant information.",
    "2.1.1": "Select whether a container signature or binary signature is required to identify the format.",
    // "2.1.2": "This is often the same as the file format name plus, if applicable, version, however some file formats may have multiple signatures. The signature name then also indicates the difference e.g. '(Big Endian)', '(Mac)'.",
    // "2.1.3": "We would like to remove this from the form",
    // "2.2.1": "Optional field if information is available. Big endian and little endian refers to the order in which the bits are written into the byte stream. Often seen in earlier 90s formats, file formats can have both a big and little endian version requiring two signatures or have one signature that is either big endian or little endian.",
    "2.2.2": "A description of the signature. To demonstrate the signature you can use hex or ascii depending on what is more human readable. The description should contain the position type, offsets and can use PRONOM syntax. It is useful to use quotation marks to differentiate the signature to the description. Words such as 'then', 'and', 'followed by' are useful for making the signature description more coherent. For example: 'BOF: Offset 2-10 then 'PNG' followed by '0x00{0-5}0105'. The description can also contain any other relevant information about the structure or pertinent information.",
    "2.2.3": "The identifying signature. The hex stream should contain no spaces. The following syntax is can be used: Normal bytes: 5FAA1401 \n\nAny number of wildcard bytes within the sequence: 5FAA*1401 \n\nSpecific number of wildcard bytes within the sequence: 5FAA{32}1401\n\nRange of wildcard bytes within the sequence: 5FAA{0-32}1401 or a variation 5FAA{32-*}1401\n\nOR choice of bytes: 5FAA(AC|DC)1401\n\nSpecific byte with a range of values: 5FAA[00:0F]1401\n\nNOT byte (i.e. wildcard that excludes a specific byte): 5FAA[!0A]1401",
    // "2.2.4": "BOF- The signature is at the beginning of the file. EOF- The signature is found at the end of the file. Variable- The signature is found at any point within the file. Variable should only be used if strictly necessary as it could slow down file format identification tools such as DROID.",
    // "2.2.5": "The minimum number of bytes from either the start or end of file where the sequence begins. Only required for position type BOF or EOF.",
    // "2.2.6": "The maximum number of bytes from either the start or end of file where the sequence begins. Only required for position type BOF or EOF.",
    "2.3.1": "This is often the same as the file format name plus, if applicable, version, however some file formats may have multiple signatures. The signature name then also indicates the difference e.g. '(Big Endian)', '(Mac)'.",
    "2.4.1": "Optional field if information is available. Big endian and little endian refers to the order in which the bits are written into the byte stream. Often seen in earlier 90s formats, file formats can have both a big and little endian version requiring two signatures or have one signature that is either big endian or little endian.",
    "2.4.2": "A description of the container signature. To demonstrate the signature you can use hex or ascii depending on what is more human readable. The description should contain the position type, offsets and can use PRONOM syntax. It is useful to use quotation marks to differentiate the signature to the description. Words such as 'then', 'and', 'followed by' are useful for making the signature description more coherent. In addition a container signature should show the pathways followed by the sequence if applicable. For example: 'tmp/syntax.xml contains BOF: Offset 2-10 then 'PNG' followed by '0x00{0-5}0105'. The description can also contain any other relevant information about the structure or pertinent information.",
    "2.4.3": "Some paths in container signatures may contain a byte sequence ",
    "2.4.4": "BOF- The signature is at the beginning of the file. EOF- The signature is found at the end of the file. Variable- The signature is found at any point within the file. Variable should only be used if strictly necessary as it could slow down file format identification tools such as DROID.",
    "2.4.5": "The minimum number of bytes from either the start or end of file where the sequence begins.",
    "2.4.6": "The maximum number of bytes from either the start or end of file where the sequence begins.",
    "2.4.7": "Identifying paths some of which may also use byte sequences for identification that defines the container signature. For example 'tmp/syntax.xml'",
    "3.1.1": "Use if a file format requires priority over another within the database, used to create hierarchy when it comes to identification i.e. if they share similar byte sequences and need additional distinction. Version 4 of a certain file format would have priority over a generic version to prevent the file format identification tool reconising the file as both.",
    "3.2.1": "Type of identifier associated with file format, for example MIME type or apple resource fork.",
    "3.2.2": "Other identifying features of the file format. For example MIME type or apple resource fork. PRONOM only accepts MIME types from official documentation or from the IANA MIME type list.",
    "3.2.3.1": "For use if a file format has an additional commonly used name.",
    "3.2.3.2": "The version associated (if any) with the alias.",
    "3.3.1": "Use if a file format has a relationship with another. For example version 2.0 could be a subsequent version of 1.0 and a prior version of 3.0.",
    "3.3.2": "The file format related to the one being entered.",
    "3.3.3": "A group of file formats often created by the same software where there are multiple versions and different types of file format all with a similar function. A good example of this is the formats from the Adobe Suite which includes PDF/A, PDF, PDF/X, Adobe Illustrator and PDF Portfolio.",
    "3.4.1.1": "Optional field if information is available. Big endian and little endian refers to the order in which the bits are written into the byte stream. Often seen in earlier 90s formats, file formats can have both a big and little endian version requiring two signatures or have one signature that is either big endian or little endian.",
    "3.4.1.2": "For use if the file format has a specific compression type.",
    "3.4.2.1": "Company, organisation or software manufacturer who created the file format.",
    "4.1": "The name by which you would wish to be contacted.",
    "4.2": "The organisation you work for or affiliate yourself with.",
    "4.3": "An email address so we can contact you to ask further questions.",
    "4.4": "Your country",
    "4.5": "Any other comments you wish to make regarding your contribution.",
    "4.6.1": "We like to credit contributers to the PRONOM database, usually this is by organisation but can also be your personal name if you prefer. Please state the name by which you'd prefer to be credited. This will mean your name appears in the release notes and as the source associated with the update or file format that you contributed to PRONOM. ",
    "4.6.2": "If you wish to remain anonymous then we will admit you from the release notes and not credit you as a source in our database.",
    "5.1": "Download a file showing the work in progress towards the next official release. This includes the file formats we have added, updated, new signatures and further details",
    "5.2.2": "Company, organisation or software manufacturer currently supporting the file format.",
    "5.2.3": "Exisitng copyright or property rights associated with the file format.",
    "5.2.4": "Name of documentation",
    "5.2.5": "The date of release for documentation.",
    "5.2.6": "For use if the documentation been made redundant or been withdrawn by the manufacturer.",
    // "5.2.7": "Date the documentation was withdrawn.",
    "5.3.1": "For use if the file format has a specific technical environment that is required to use it.",
    "6.1": "Date of change/ date of new entry",
    "6.2": "The contributor who suggested the change",
    "6.3": "A short summary of what has been changed and any relevant reasoning.",
    // "6.4": "Use if a file format requires priority over another within the database, i.e. if they share similar byte sequences and need additional distinction."

  },
  display: {
    "1.1.1": "This is the name of the file format in the specification, or if not known then the name that best describes the format. Each word is capatilised, unless the specification says otherwise e.g. 'exFAT'. If the format has multiple names these may be placed in the alias field.",
    // "1.1.2": "This field indicates the version number of the file format. There might not be a version. If it is a version number then it is written as a numeral e.g. '1' or '1.1'. Sometimes the version number is a range, e.g. 1-5. Sometimes versions are not numbers in which case use the standard text e.g. 'Alpha' or if multiple versions of the same file format are in the database it may be written in as 'Generic'.",
    "1.1.3": "A description of the the file format. Descriptions are objective, no commercial statements e.g. 'this software is best for'. Online sources may be used, preferably multiple sources. Areas that could be covered in the description include a timeline of development and support, the function of the file format and software and details of the format specification. The description should also include information relevant to it's identification, preservation and the conditions you might encounter it. For example what different extensions may refer to, whether the size of the file has any relevance or if other files are normally found in conjunction with this format.",
    "1.1.4": "The format type that most closely resembles the format.",
    "1.2.1": "The name of the documentation, standard or webpage.",
    "1.2.2": "The link to the webpage or document.",
    "1.2.3": "The full name of the documentation",
    "1.2.4": "Author of the article as it appears on the webpage or the documentation.",
    "1.2.5": "Other identifying features of the file format. For example MIME type or apple resource fork. PRONOM only accepts MIME types from official documentation or from the IANA MIME type list.",
    "1.2.6": "The date of publication on the website or documentation.",
    "1.2.7": "Format of the publication. This could be document or website.",
    "1.2.8": "Any other relevant information.",
    "2.1.1": "Shows if a container signature or binary signature is required to identify the format.",
    "2.1.2": "This is often the same as the file format name plus, if applicable, version, however some file formats may have multiple signatures. The signature name then also indicates the difference e.g. '(Big Endian)', '(Mac)'.",
    "2.1.3": "We would like to remove this from the form",
    "2.2.1": "Optional field if information is available. Big endian and little endian refers to the order in which the bits are written into the byte stream. Often seen in earlier 90s formats, file formats can have both a big and little endian version requiring two signatures or have one signature that is either big endian or little endian.",
    "2.2.2": "A description of the signature. To demonstrate the signature, hex or ascii may be used, depending on what is more human readable. The description contains the position type, offsets and can use PRONOM syntax. Quotation marks are used to differentiate the signature from the description. Words such as 'then', 'and', 'followed by' are useful for making the signature description more coherent. For example: 'BOF: Offset 2-10 then 'PNG' followed by '0x00{0-5}0105'. The description may also contain any other relevant information about the structure or pertinent information.",
    "2.2.3": "The identifying signature. The following syntax is used: Normal bytes: 5FAA1401 \n\nAny number of wildcard bytes within the sequence: 5FAA*1401 \n\nSpecific number of wildcard bytes within the sequence: 5FAA{32}1401\n\nRange of wildcard bytes within the sequence: 5FAA{0-32}1401 or a variation 5FAA{32-*}1401\n\nOR choice of bytes: 5FAA(AC|DC)1401\n\nSpecific byte with a range of values: 5FAA[00:0F]1401\n\nNOT byte (i.e. wildcard that excludes a specific byte): 5FAA[!0A]1401",
    "2.2.4": "BOF- The signature at the beginning of the file. EOF- The signature at the end of the file. Variable- The signature found at any point within the file. Variable is only be used if strictly necessary as it could slow down file format identification tools such as DROID.",
    "2.2.5": "The minimum number of bytes from either the start or end of file where the sequence begins. Only required for position type BOF or EOF.",
    "2.2.6": "The maximum number of bytes from either the start or end of file where the sequence begins. Only required for position type BOF or EOF.",
    "2.3.1": "This is often the same as the file format name plus, if applicable, version, however some file formats may have multiple signatures. The signature name then also indicates the difference e.g. '(Big Endian)', '(Mac)'.",
    "2.4.1": "Optional field if information is available. Big endian and little endian refers to the order in which the bits are written into the byte stream. Often seen in earlier 90s formats, file formats can have both a big and little endian version requiring two signatures or have one signature that is either big endian or little endian.",
    "2.4.2": "A description of the container signature. To demonstrate the signature hex or ascii may be used depending on what is more human readable. The description contains the position type, offsets and can use PRONOM syntax. Quotation marks are used to differentiate the signature to the description. Words such as 'then', 'and', 'followed by' are useful for making the signature description more coherent. In addition a container signature shows the pathways followed by the sequence if applicable. For example: 'tmp/syntax.xml contains BOF: Offset 2-10 then 'PNG' followed by '0x00{0-5}0105'. The description may also contain any other relevant information about the structure or pertinent information.",
    "2.4.3": "Some paths in container signatures may contain a byte sequence ",
    "2.4.4": "BOF- The signature is at the beginning of the file. EOF- The signature is found at the end of the file. Variable- The signature is found at any point within the file. Variable is only be used if strictly necessary as it could slow down file format identification tools such as DROID.",
    "2.4.5": "The minimum number of bytes from either the start or end of file where the sequence begins.",
    "2.4.6": "The maximum number of bytes from either the start or end of file where the sequence begins.",
    "2.4.7": "Identifying paths some of which may also use byte sequences for identification that defines the container signature. For example 'tmp/syntax.xml'",
    "3.1.1": "Used if a file format requires priority over another within the database, used to create hierarchy when it comes to identification i.e. if they share similar byte sequences and need additional distinction. Version 4 of a certain file format would have priority over a generic version to prevent the file format identification tool reconising the file as both.",
    "3.2.1": "Type of identifier associated with file format, for example MIME type or apple resource fork.",
    "3.2.2": "Other identifying features of the file format. For example MIME type or apple resource fork. PRONOM only accepts MIME types from official documentation or from the IANA MIME type list.",
    "3.2.3.1": "For use if a file format has an additional commonly used name.",
    "3.2.3.2": "The version associated (if any) with the alias.",
    "3.3.1": "Used if a file format has a relationship with another. For example version 2.0 could be a subsequent version of 1.0 and a prior version of 3.0.",
    "3.3.2": "The file format related to the one displayed.",
    "3.3.3": "A group of file formats often created by the same software where there are multiple versions and different types of file format all with a similar function. A good example of this is the formats from the Adobe Suite which includes PDF/A, PDF, PDF/X, Adobe Illustrator and PDF Portfolio.",
    "3.4.1.1": "Big endian and little endian refers to the order in which the bits are written into the byte stream. Often seen in earlier 90s formats, file formats can have both a big and little endian version requiring two signatures or have one signature that is either big endian or little endian.",
    "3.4.1.2": "Used if the file format has a specific compression type.",
    "3.4.2.1": "Company, organisation or software manufacturer who created the file format.",
    "4.1": "The name of the contact.",
    "4.2": "The organisation associated with the contact.",
    "4.3": "An email address to ask further questions.",
    "4.4": "Your Country",
    "4.5": "Any other comments regarding the contribution.",
    "4.6.1": "We like to credit contributers to the PRONOM database, usually this is by organisation but can also be your personal name if you prefer. Please state the name by which you'd prefer to be credited. This will mean your name appears in the release notes and as the source associated with the update or file format that you contributed to PRONOM. ",
    "4.6.2": "If you wish to remain anonymous then we will admit you from the release notes and not credit you as a source in our database.",
    "5.1": "Download a file showing the work in progress towards the next official release. This includes the file formats added, updated, new signatures and further details",
    "6.1": "A file extension is the suffix at the end of a filename.",
    "7.1": "The physical form of the archival materials.",
    "8.1": "The PRONOM Persistent Unique Identifier (PUID) is an extensible scheme for providing persistent, unique and unambiguous identifiers for records in the PRONOM registry.",
    "9.1": "A media type (also known as a Multipurpose Internet Mail Extensions or MIME type) indicates the nature and format of a document, file, or assortment of bytes. "
  }
}



// UNUSED TOOLTIPS

    // "3.1": "Use if a file format requires priority over another within the database, i.e. if they share similar byte sequences and need additional distinction. Also use if a file format has a relationship with another. For example version 2.0 could be a subsequent version of 1.0 and a prior version of 3.0.",
    // "4.1": "Type of identifier associated with file format, for example MIME type or apple resource fork.",
    // "4.2": "Other identifying features of the file format. For example MIME type or apple resource fork. PRONOM only accepts MIME types from official documentation or from the IANA MIME type list.",
    // "4.3.2": "The version associated (if any) with the alias.",
    // "5.1.1": "Optional field if information is available. Big endian and little endian refers to the order in which the bits are written into the byte stream. Often seen in earlier 90s formats, file formats can have both a big and little endian version requiring two signatures or have one signature that is either big endian or little endian.",
    // "5.1.2": "For use if the file format has a specific compression type.",
    // "5.2.1": "Company, organisation or software manufacturer who created the file format.",