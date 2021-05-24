public class TxHandler {

    private UTXOPool utxoPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        UTXOPool currentPool = new UTXOPool(this.utxoPool);
        double inputValuesSum = 0;
        double outputValuesSum = 0;

        int inputIndex = 0;
        for (Transaction.Input input: tx.getInputs()) {
            UTXO prevUtxo = new UTXO(input.prevTxHash, input.outputIndex);
            
            // validate that the output pointed is present.
            if (!utxoPool.contains(prevUtxo)) {
                return false;
            }

            Transaction.Output prevOutput = utxoPool.getTxOutput(prevUtxo);

            if (!Crypto.verifySignature(prevOutput.signature, 
                tx.getRawDataToSign(inputIndex), 
                input.signature)) {
                 return false;
            }

            inputValuesSum += utxoPool.getTxOutput(prevUtxo).value;
            utxoPool.removeUTXO(prevUtxo);

            inputIndex++;
        }
        for (Transaction.Output output: tx.getOutputs()) {
            if (output.value < 0) {
                return false;
            }
            outputValuesSum += output.value;
        }

        return outputValuesSum <= inputValuesSum;

    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    }

    private boolean validateInput(Transaction.Input input, UTXO prevUtxo, UTXOPool utxoPool) {
        
        
        //validate Signature of the input
        PublicKey prevOwnersPublicKey = prevTx.getOutputs().get(input.outputIndex).address;
        if (!Crypto.verifySignature(prevOwnersPublicKey, prevTx.getRawDataToSign(input.outputIndex), input.signature)) {
            return false;
        }

        return true;
    }

    private double getValueFromPrevOutput(Transaction.Input input, UTXOPool utxoPool) {
        return utxoPool.getTxOutput(new UTXO(input.prevTxHash, input.outputIndex)).value;
    }

    private void removeUsedOutput(Transaction.Input input, UTXOPool utxoPool) {
        // Remove utxo so that it is not double spent.
        utxoPool.removeUTXO(new UTXO(input.prevTxHash, input.outputIndex));
    }

}
