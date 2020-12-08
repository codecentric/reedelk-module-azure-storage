# Reedelk Module Azure Storage

Azure Storage components and connectors for Reedelk Integration Platform: https://www.reedelk.com/documentation

# Important
This project uses a patched version of 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml' (https://github.com/reedelk/jackson-dataformat-xml).
This is necessary to fix a bug in the XMLInputFactory/XMLOutputFactory default implementation which causes 'PagedIterable' to loop indefinitely.
On OSGi the default class is 'XMLInputFactoryImpl'. With the patched jackson-dataformat-xml we are forcing to use com.ctc.wstx.stax.WstxInputFactory and com.ctc.wstx.stax.WstxOutputFactory 
which are handling correctly the issue.

The Azure Blob Client does not allow to specifically set the XML Input/Output factory, hence the need to manually patch the Jackson dataformat-xml library. 

References:
https://github.com/Azure/azure-sdk-for-java/issues/9465
https://github.com/Azure/azure-sdk-for-java/issues/11104
